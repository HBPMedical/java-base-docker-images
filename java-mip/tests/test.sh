#!/usr/bin/env bash

set -o pipefail  # trace ERR through pipes
set -o errtrace  # trace ERR through 'time command' and other functions
set -o errexit   ## set -e : exit the script if any statement returns a non-true return value

get_script_dir () {
     SOURCE="${BASH_SOURCE[0]}"

     while [ -h "$SOURCE" ]; do
          DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
          SOURCE="$( readlink "$SOURCE" )"
          [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
     done
     cd -P "$( dirname "$SOURCE" )"
     pwd
}

ROOT_DIR="$(get_script_dir)/../.."

echo "Starting the results database..."
$ROOT_DIR/tests/analytics-db/start-db.sh
echo
echo "Starting the local database..."
$ROOT_DIR/tests/dummy-ldsm/start-db.sh
echo

function _cleanup() {
  local error_code="$?"
  echo "Stopping the databases..."
  $ROOT_DIR/tests/analytics-db/stop-db.sh
  $ROOT_DIR/tests/dummy-ldsm/stop-db.sh
  exit $error_code
}
trap _cleanup EXIT INT TERM

sleep 2

if groups $USER | grep &>/dev/null '\bdocker\b'; then
  DOCKER="docker"
else
  DOCKER="sudo docker"
fi

 $DOCKER run --rm $OPTS \
   --link dummyldsm:indb \
   --link analyticsdb:outdb \
   -e NODE=job_test \
   -e IN_JDBC_DRIVER=org.postgresql.Driver \
   -e IN_JDBC_URL=jdbc:postgresql://indb:5432/postgres \
   -e IN_JDBC_USER=postgres \
   -e IN_JDBC_PASSWORD=test \
   -e OUT_JDBC_DRIVER=org.postgresql.Driver \
   -e OUT_JDBC_URL=jdbc:postgresql://outdb:5432/postgres \
   -e OUT_JDBC_USER=postgres \
   -e OUT_JDBC_PASSWORD=test \
   -e RESULT_TABLE=job_result \
   -e OUT_FORMAT=INTERMEDIATE_RESULTS \
   -e PARAM_query="select prov, left_amygdala, right_poparoper from brain" \
   -e PARAM_variables=prov \
   -e PARAM_covariables=left_amygdala,right_poparoper \
   -e PARAM_grouping= \
   hbpmip/java-rapidminer-build test