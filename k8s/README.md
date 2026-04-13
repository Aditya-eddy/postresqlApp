# Kubernetes manifests

Deploys the travel-card-api and its Postgres dependency into the `travelcard` namespace.

## Contents

- `namespace.yaml` — `travelcard` namespace
- `postgres-*.yaml` — Postgres 15 StatefulSet + headless Service, init SQL mounted from ConfigMap, credentials from Secret, 1Gi PVC
- `travel-card-api-*.yaml` — App Deployment + ClusterIP Service, non-secret config in ConfigMap, secrets in Secret
- `kustomization.yaml` — applies everything in the right order

## Apply

```sh
kubectl apply -k k8s/
```

## Notes

- Container image in `travel-card-api-deployment.yaml` is `aditya-eddy/travel-card-api:latest`. Build and push the image (or change the reference) before applying.
- Secrets in `travel-card-api-secret.yaml` and `postgres-secret.yaml` contain development defaults. Replace them (or use an external secrets manager) before deploying anywhere real.
- The service is `ClusterIP` only. Add an Ingress or change to `LoadBalancer`/`NodePort` to expose externally.
