## DieselAgent

The DieselAgent provides a web based statistics console for a running Calypso server. It also provides a Restful API and therefore a way to access the Calypso API without needing the calypso client side code or Java.

Initially the DieselAgent was developed as a way of performing regression across two Calypso instances.

## Installation and Running

You will need maven to build and run the DieselAgent.

1. Download the source to your local drive.
2. Add the following jar files from your Calypso distribution into lib folder.
  * calypso-core.jar
  * calypso-logging.jar
  * calypso.jar
  * calypsoml-core.jar
  * calypsoml-impl.jar
3. Add your calypsosystem.properties.* files to the lib folder.
4. Create an ENVS.txt file in the src/main/resources folder. This file should contain a comma seperated list of your environment names. (See ENVS-sample.txt).
5. You can run `mvn clean package` to build the agent.
6. To run the agent execute the command `mvn jetty:run`
7. You can access the Web based status console at `http://localhost:8080/test-agent`





