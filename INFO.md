Instagram Registration
======================

    Client ID 	b90fd0e7168c45c3a72f41ba2214a66e
    Client Secret 	6fac06a426394034ab839bcc803a4d8f
    Website URL 	http://dragon-alien.codio.io:3000/
    Redirect URI 	http://dragon-alien.codio.io:3000/signup-result
    
Steps to get followers of an account
====================================

1. Direct the user to authentication url:

    https://api.instagram.com/oauth/authorize/?client_id=b90fd0e7168c45c3a72f41ba2214a66e&redirect_uri=http%3A%2F%2Fdragon-alien.codio.io%3A3000%2Fsignup-result&response_type=code
    
2. Receive the code or error:

    http://dragon-alien.codio.io:3000/session?session=123&code=CODE
    http://dragon-alien.codio.io:3000/session?session=123&error=access_denied&error_reason=user_denied&error_description=The+user+denied+your+request
    
3. Get the access token:

    curl \
    -F 'client_id=CLIENT-ID' \
    -F 'client_secret=CLIENT-SECRET' \
    -F 'grant_type=authorization_code' \
    -F 'redirect_uri=http://dragon-alien.codio.io:3000/session' \
    -F 'code=CODE' \
    https://api.instagram.com/oauth/access_token

4. Get the followers

    TODO
