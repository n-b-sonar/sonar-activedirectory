/*
 * SonarQube Active Directory Plugin
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.activedirectoy.windows.sso.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.plugins.activedirectoy.windows.WindowsAuthenticationHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class SsoValidationFilterTest {
  private SsoValidationFilter ssoValidationFilter;
  private WindowsAuthenticationHelper windowsAuthenticationHelper;

  @Before
  public void init() {
    windowsAuthenticationHelper = Mockito.mock(WindowsAuthenticationHelper.class);
    ssoValidationFilter = new SsoValidationFilter(windowsAuthenticationHelper);
  }

  @Test
  public void doGetPatternTest() {
    assertThat(ssoValidationFilter.doGetPattern().getUrl()).isEqualTo("/active_directory/validate");
  }

  @Test
  public void doFilterTest() throws IOException, ServletException {
    runDoFilterTest(true);
    runDoFilterTest(false);
  }

  private void runDoFilterTest(boolean isUserAuthenticated) throws IOException, ServletException {

    HttpSession httpSession = Mockito.mock(HttpSession.class);
    HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    Mockito.when(servletRequest.getSession()).thenReturn(httpSession);

    HttpServletResponse servletResponse = Mockito.mock(HttpServletResponse.class);

    FilterChain filterChain = Mockito.mock(FilterChain.class);

    Mockito.when(windowsAuthenticationHelper.isUserSsoAuthenticated(servletRequest)).thenReturn(isUserAuthenticated);

    ssoValidationFilter.doFilter(servletRequest, servletResponse, filterChain);

    if (isUserAuthenticated) {
      Mockito.verify(filterChain, Mockito.times(1)).doFilter(servletRequest, servletResponse);
    } else {
      Mockito.verify(servletResponse, Mockito.times(1)).sendRedirect("/active_directory/unauthorized");
    }
  }
}
