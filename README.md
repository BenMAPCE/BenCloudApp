# BenCloudApp 

The BenMAP web tool is comprised of two components: the API and the User Interface (UI).

The API is implemented as a Java-based REST service and is hosted in the BenCloudServer repository.

The UI is implemented as a Quasar-based single page application and is hosted in the BenCloudApp repository.

Both components are implemented in EPA's Kubernetes-based DevSecOps environment. In that context, they run as docker containers deployed via Gitlab CI/CD. Details can be found in the docker and k8s folders as well as the .gitlab-ci.yml file.

Outside of this environment, each tool can be run directly and can be hosted on most platforms for local development, testing, or analysis work.

## Install the dependencies
```bash
Change to the bencloud-quasar folder and execute the following command

npm install

npm install -g @quasar/cli
```

### Start the app in development mode (hot-code reloading, error reporting, etc.)
```bash
# For Linux and Mac
QENV=development quasar dev

# For Windows
set QENV='development' & quasar dev
```

### Lint the files
```bash
npm run lint
```

### Build the app for production
```bash
quasar build
```

### Customize the configuration
See [Configuring quasar.conf.js](https://v2.quasar.dev/quasar-cli/quasar-conf-js).
