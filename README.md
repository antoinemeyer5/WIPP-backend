# WIPP REST API
A Java Spring Boot application for managing WIPP data and workflows. 
The API follows the HATEOAS architecture using the HAL format.

## Requirements
Requirements for development environment setup.

### Java environment
* Java JDK 21
* Maven version compatible with Java 21

### Database
* MongoDB (Supported versions: 3.6 to 7.0)

### Identity and Access Management
* Keycloak 11.0.2
* Default dev configuration expects Keycloak at `http://localhost:8081/auth` (see 
`wipp-backend-application/src/main/resources/application.properties`. Sample 
Docker run command:
`docker run -p 8081:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:11.0.2` (see https://www.keycloak.org/getting-started/getting-started-docker)
* Import WIPP realm available in folder `docs/auth-acl`
* RBAC-ACLs descriptions available in file `docs/auth-acl/acl.md`

### Kubernetes cluster
* For development purposes, a single-node cluster can be easily installed using [Minikube](https://github.com/kubernetes/minikube) or [Docker for Mac on macOS](https://docs.docker.com/docker-for-mac/#kubernetes)
* We are using [Argo Workflows](https://argo-workflows.readthedocs.io/en/latest/) to manage workflows on a Kubernetes cluster, 
a minimal installation of the Argo Server and Controller can be set up using the following commands:
```shell
kubectl apply -f https://github.com/argoproj/argo-workflows/releases/download/v3.5.5/quick-start-minimal.yaml
```
Please follow the instructions for version 3.5.5 [here](https://github.com/argoproj/argo-workflows/releases/tag/v3.5.5) to:
 - install the Argo binary

### Data storage
* Create a `WIPP-plugins` folder in your home directory for data storage (`dev` Maven profile is expecting the data folder location to be `$HOME/WIPP-plugins`)
* Create the WIPP data storage Persistent Volume (PV) and Persistent Volume Claim (PVC) in your Kubernetes cluster following the templates for hostPath PV and PVC available in [the WIPP repository](https://github.com/usnistgov/WIPP/tree/master/deployment/wipp-ci-single-node/volumes):
    * `path` of `hostPath` in `hostPath-wippdata-volume.yaml` should be modified to match path of `WIPP-plugins` folder created above
    * `storage` of `capacity` is set to 100Gi by default, this value can be modified in `hostPath-wippdata-volume.yaml` and `hostPath-wippdata-pvc.yaml`
    * run `hostPath-deploy.sh` to setup the WIPP data PV and PVC

**Local backend import option**  
Default root folder configuration for the local backend import of images collections is:
* `${user.home}/WIPP-plugins/local-import` in `dev` profile and
* `/data/WIPP-plugins/local-import` in `prod` profile

This default configuration can be changed in `wipp-backend-application/pom.xml` (property `storage.local.import`) when running locally or in the deployment 
manifest when running in a Kubernetes cluster (an additional volume needs to be mounted if the new location is not in the WIPP root data folder).

## Compiling
```shell
mvn clean install
```
## Running
```shell
cd wipp-backend-application
mvn spring-boot:run
```
- The WIPP REST API will be launched with the `dev` profile and available at http://localhost:8080/api  

- Swagger API documentation will be available at:
 - http://localhost:8080/api/swagger-ui/index.html (Swagger UI) and,
 - http://localhost:8080/api/v3/api-docs (OpenAPI spec)

## Docker packaging
```sh
docker build --no-cache . -t wipp_backend
```
For a Docker deployment of WIPP on a Kubernetes cluster, scripts and configuration files are available in the [WIPP repository](https://github.com/usnistgov/WIPP/tree/master/deployment).

## Deployment
1. Create a `.env` file in the root of the repository, using `sample-env` as an example.
1. Configure `kubectl` with a `kubeconfig` pointing to the correct Kubernetes cluster. Optionally, pass the location of the `kubeconfig` file in the `.env`. This value defaults to the standard `kubeconfig` location. 
1. Run the script using: `./deploy.sh`.

### Application Performance Monitoring (APM)
The Elastic APM Java agent is integrated into the Docker image as an optional setting. The Elastic APM agent will push metrics to an APM server, which feeds into Elasticsearch and Kibana. Configuration of the Elastic APM Java Agent to connect to the APM server is controlled via environment variables. These variables are optional if Elastic APM is not needed.

| Environment Variable   | Description |
| ------------- | ------------- |
| ELASTIC_APM_SERVICE_NAME  | Service name tag attached to all metrics sent. |
| ELASTIC_APM_SERVER_URLS  | Url of Elastic APM server.  |
| ELASTIC_APM_APPLICATION_PACKAGES | (Optional) Determines stack trace frame. Multiple packages can be set. |
| ELASTIC_APM_SECRET_TOKEN  | (Optional) Secret token for Elastic APM server. | 

## WIPP Development flow
We are following the [Gitflow branching model](https://nvie.com/posts/a-successful-git-branching-model/) for the WIPP development.  

### Contributing
Please follow the [Contributing guidelines](CONTRIBUTING.md)

## Disclaimer/License

[NIST Disclaimer/License](LICENSE.md)
