package dwbh.api.services;

import dwbh.api.domain.Login;
import dwbh.api.domain.LoginInput;
import dwbh.api.domain.Result;
import dwbh.api.domain.User;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
public interface SecurityService {

  /**
   * Looks for the user matching the provided token
   *
   * @param token the token provided for the client
   * @return an instance of type {@link User} or null if the token doesn't match any user
   * @since 0.1.0
   */
  User findUserByToken(String token);

  /**
   * Processes a user's login
   *
   * @param input credentials information
   * @return an instance of {@link Result} ({@link Login} | {@link Error})
   * @since 0.1.0
   */
  Result<Login> login(LoginInput input);
}
