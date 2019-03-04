package dwbh.api.graphql.instrumentation;

import dwbh.api.domain.ErrorConstants;
import dwbh.api.graphql.Context;
import dwbh.api.graphql.ResultUtils;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldParameters;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import java.util.Optional;

/**
 * Responsible for handling the rules over the @anonymousAllowed directive which are:
 *
 * <ul>
 *   <li>By default all queries are authenticated
 *   <li>Only queries annotated with @anonymousAllowed and their children are allowed to be accessed
 *       anonymously
 * </ul>
 *
 * @since 0.1.0
 * @see ErrorConstants#BAD_CREDENTIALS
 */
public class Anonymous extends SimpleInstrumentation {

  @Override
  public InstrumentationState createState(InstrumentationCreateStateParameters params) {
    return new AnonymousState(false);
  }

  @Override
  public InstrumentationContext<ExecutionResult> beginField(InstrumentationFieldParameters params) {
    AnonymousState state = params.getInstrumentationState();
    state.setAllowed(isAllowed(params));

    return super.beginField(params);
  }

  @Override
  public DataFetcher<?> instrumentDataFetcher(
      DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters params) {
    boolean isAllowed = isAllowed(params);

    return isAllowed ? super.instrumentDataFetcher(dataFetcher, params) : renderBadCredentials();
  }

  private boolean isAllowed(InstrumentationFieldParameters params) {
    AnonymousState state = params.getInstrumentationState();
    Context context = (Context) params.getExecutionContext().getContext();
    GraphQLFieldDefinition fieldDefinition = params.getField();

    return isAllowed(state, fieldDefinition, context);
  }

  private boolean isAllowed(InstrumentationFieldFetchParameters parameters) {
    AnonymousState state = parameters.getInstrumentationState();
    Context context = parameters.getEnvironment().getContext();
    GraphQLFieldDefinition fieldDefinition = parameters.getField();

    return isAllowed(state, fieldDefinition, context);
  }

  private boolean isAllowed(AnonymousState state, GraphQLFieldDefinition definition, Context ctx) {
    boolean hierarchyAllowed =
        Optional.ofNullable(state).map(AnonymousState::isAllowed).orElse(false);

    boolean userPresent =
        Optional.ofNullable(ctx).flatMap(Context::getAuthenticatedUser).isPresent();

    boolean directivePresent =
        Optional.ofNullable(definition.getDefinition().getDirective("anonymousAllowed"))
            .isPresent();

    return hierarchyAllowed || userPresent || directivePresent;
  }

  private DataFetcher<DataFetcherResult<?>> renderBadCredentials() {
    return (env) -> ResultUtils.render(ErrorConstants.BAD_CREDENTIALS);
  }
}
