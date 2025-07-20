#!/bin/bash

cd deploy/k8s
kubectl apply -f namespace.yaml
kubectl apply -f secret.yaml
kubectl apply -f pvc.yaml
kubectl apply -R -f storage
#helm install kafka bitnami/kafka -f helm/kafka-values.yaml -n contest
kubectl apply -R -f main
helm install ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx -f helm/ingress-values.yaml
kubectl apply -f ingress.yaml