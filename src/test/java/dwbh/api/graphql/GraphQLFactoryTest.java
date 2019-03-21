/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.graphql;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.fetchers.FetcherProvider;
import dwbh.api.fetchers.GroupFetcher;
import dwbh.api.fetchers.SecurityFetcher;
import dwbh.api.fetchers.UserFetcher;
import dwbh.api.fetchers.UserGroupFetcher;
import dwbh.api.fetchers.VotingFetcher;
import graphql.ExecutionInput;
import io.micronaut.core.io.ResourceResolver;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GraphQLFactoryTest {

  @Test
  void testCreateSchema() {
    // given: the fetcher provider
    var fetchers = new FetcherProvider();

    // and: mocking group fetcher behavior
    var groupFetcher = mock(GroupFetcher.class);
    when(groupFetcher.listMyGroups(any())).thenReturn(randomListOf(2, Group.class));

    // and: adding group fetcher to fetcher providers
    fetchers.setGroupFetcher(groupFetcher);

    // and: adding voting fetcher to fetcher providers
    fetchers.setVotingFetcher(mock(VotingFetcher.class));

    // and: adding user fetcher to fetcher providers
    fetchers.setUserFetcher(mock(UserFetcher.class));

    // and: adding user group fetcher to fetcher providers
    fetchers.setUserGroupFetcher(mock(UserGroupFetcher.class));

    // and: adding security fetcher to fetcher providers
    fetchers.setSecurityFetcher(mock(SecurityFetcher.class));

    // when: creating a valid GraphQL engine
    var resolver = new ResourceResolver();
    var graphQLEngine = new GraphQLFactory().graphQL(resolver, fetchers);

    // and: querying the schema with an authenticated user
    var context = new Context();
    context.setAuthenticatedUser(new User());
    var executionInput =
        ExecutionInput.newExecutionInput()
            .query("{ listMyGroups { name } }")
            .context(context)
            .build();
    var result = graphQLEngine.execute(executionInput);
    Map<String, List<Map<String, ?>>> data = result.getData();

    var groupList = data.get("listMyGroups");

    // then: we should get the expected result
    assertEquals(groupList.size(), 2);
  }

  @Test
  void testWrongResourcePath() {
    assertThrows(
        Throwable.class, () -> new GraphQLFactory().graphQL(any(), mock(FetcherProvider.class)));
  }
}
