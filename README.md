Dynamic SOA Platform
====================
DSOA is a new service-component based platform that intends to support the execution of Quality-Aware Service-Based Applications (QSBAs).
These applications are designed as service compositions, which are dynamically configured based on available services and corresponding
quality of service specifications. To do that, DSOA defines a new component type, which represent application building blocks.

Building instructions
=====================
Right click dsoa-platform.bnd and select makebundle. This option will compile and zip the platform elements. Besides that, it will build
a manifest file which defines the new component type:

IPOJO-Extension: dsoa-component:br.ufpe.cin.dsoa.platform.component.DsoaComponentType

Since DSOA is built atop of iPojo, generated bundle (dsoa-platform.jar) needs to be manipulated in order to modify the classfiles to turn
them iPojo components and to include corresponding meta-data into the manifest file.

Important:
==========
You should use jdk1.7.0 (there are some problems with newer versions.

Starting DSOA Platform:
=======================


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