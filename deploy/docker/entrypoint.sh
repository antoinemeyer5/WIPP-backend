#!/bin/sh

if [[ -z $KEYCLOAK_AUTH_URL ]]; then
  echo "Error: missing KEYCLOAK_AUTH_URL env var, exiting."
  exit 1
fi

if [[ -z $SPRING_DATA_MONGODB_HOST ]]; then
  echo "No SPRING_DATA_MONGODB_HOST env var set, using default value."
fi

if [[ -z $SPRING_DATA_MONGODB_PORT ]]; then
  echo "No SPRING_DATA_MONGODB_PORT env var set, using default value."
fi

if [[ -z $KUBE_WIPPDATA_PVC ]]; then
  echo "No KUBE_WIPPDATA_PVC env var set, using default value."
fi

if [[ -z $OME_CONVERTER_THREADS ]]; then
  echo "No OME_CONVERTER_THREADS env var set, using default value."
fi

if [[ -n ${ELASTIC_APM_SERVER_URLS} && -n ${ELASTIC_APM_SERVICE_NAME} ]]; then
  export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -javaagent:/opt/wipp/elastic-apm-agent.jar"
  export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Delastic.apm.service_name=$ELASTIC_APM_SERVICE_NAME"
  export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Delastic.apm.application_packages=$ELASTIC_APM_APPLICATION_PACKAGES"
  export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Delastic.apm.server_urls=$ELASTIC_APM_SERVER_URLS"
fi

java -jar /opt/wipp/wipp-backend.war