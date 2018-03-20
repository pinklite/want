The /nettester directory should be placed on the host outside of the 
(Tomcat) $CATALINA_HOME/webapps directory. In addition, the environment variable 
'NETTESTER_HOME' must be set to the path of the /nettester directory.

The nettester/nettester_creds.txt file is a space-delimitted file that 
contains multiple usernames and passwords:

<username1>[space]<password1>
<username2>[space]<password2>
<username3>[space]<password3>
...

