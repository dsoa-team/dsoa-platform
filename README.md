Dynamic SOA Platform
====================

Start bundles: 

start file:apps/cglib-nodep-2.2.jar
start file:apps/antlr-runtime-3.1.1.jar
start file:apps/commons-logging-1.1.1.jar
start file:apps/esper-4.7.0.jar
start file:apps/monitoradmin-1.0.2.jar
start file:apps/dsoa-platform.jar
start file:apps/configuration-bundle.jar


start file:apps/HomebrokerModel.jar
start file:apps/HomebrokerBB.jar

start file:apps/HomebrokerBBClient.jar

LDAP QUERIES:

A quick guide to using LDAP queries
Perform attribute matching:
(name=John Smith)
(age>=20)
(age<=65)
Perform fuzzy matching:
(name~=johnsmith)
Perform wildcard matching:
(name=Jo*n*Smith*)
Determine if an attribute exists:
(name=*)
Match all the contained clauses:
(&(name=John Smith)(occupation=doctor))
Match at least one of the contained clauses:
(|(name~=John Smith)(name~=Smith John))
Negate the contained clause:
(!(name=John Smith))