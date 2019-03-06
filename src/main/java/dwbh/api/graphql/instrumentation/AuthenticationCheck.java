package dwbh.api.graphql.instrumentation;

import dwbh.api.domain.ErrorConstants;
import dwbh.api.graphql.Context;
import dwbh.api.graphql.ResultUtils;
import dwbh.api.services.internal.FunctionsUtils;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Responsible for handling the rules over the @anonymousAllowed directive which are:
 *
 * <ul>
 *   <li>By default all queries require authentication
 *   <li>Introspection queries are allowed to be accessed anonymously
 *   <li>Queries annotated with @anonymousAllowed are allowed to be accessed anonymously
 *   <li>Fields with parents annotated with @anonymousAllowed are allowed as well
 * </ul>
 *
 * @since 0.1.0
 * @see ErrorConstants#BAD_CREDENTIALS
 */
public class AuthenticationCheck extends SimpleInstrumentation {

  private static final String FIELD_INTROSPECTION = "__schema";
  private static final String DIRECTIVE_ANONYMOUS = "anonymousAllowed";
  private static final Predicate<CheckerParams> CHECKERS =
      FunctionsUtils.any(
          AuthenticationCheck::isHierarchyAllowed,
          AuthenticationCheck::isUserPresent,
          AuthenticationCheck::isDirectivePresent,
          AuthenticationCheck::isIntrospection);

  /**
   * CheckerParams required to evaluate the authentication check
   *
   * @since 0.1.0
   */
  private static class CheckerParams {
    private final transient AuthenticationCheckState state;
    private final transient Context context;
    private final transient GraphQLFieldDefinition field;

    private CheckerParams(
        AuthenticationCheckState state, Context context, GraphQLFieldDefinition field) {
      this.state = state;
      this.context = context;
      this.field = field;
    }
  }

  @Override
  public InstrumentationState createState(InstrumentationCreateStateParameters params) {
    return new AuthenticationCheckState(false);
  }

  @Override
  public DataFetcher<?> instrumentDataFetcher(
      DataFetcher<?> fetcher, InstrumentationFieldFetchParameters params) {
    AuthenticationCheckState state = params.getInstrumentationState();
    Context context = params.getEnvironment().getContext();
    GraphQLFieldDefinition fieldDefinition = params.getField();

    var isAllowed = CHECKERS.test(new CheckerParams(state, context, fieldDefinition));
    state.setAllowed(isAllowed);

    return isAllowed ? super.instrumentDataFetcher(fetcher, params) : renderBadCredentials();
  }

  @SuppressWarnings("unchecked")
  private static <A> DataFetcher<A> renderBadCredentials() {
    return (env) -> (A) ResultUtils.render(ErrorConstants.BAD_CREDENTIALS);
  }

  private static boolean isIntrospection(CheckerParams cond) {
    return Optional.ofNullable(cond.field)
        .map(GraphQLFieldDefinition::getName)
        .map(name -> name.equals(FIELD_INTROSPECTION))
        .orElse(false);
  }

  private static boolean isHierarchyAllowed(CheckerParams cond) {
    return Optional.ofNullable(cond.state).map(AuthenticationCheckState::isAllowed).orElse(false);
  }

  private static boolean isUserPresent(CheckerParams cond) {
    return Optional.ofNullable(cond.context).flatMap(Context::getAuthenticatedUser).isPresent();
  }

  private static boolean isDirectivePresent(CheckerParams cond) {
    return Optional.ofNullable(cond.field)
        .map(GraphQLFieldDefinition::getDefinition)
        .map(def -> def.getDirective(DIRECTIVE_ANONYMOUS))
        .isPresent();
  }
}
