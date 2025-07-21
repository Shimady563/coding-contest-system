#!/bin/bash

#helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

cd deploy/k8s
kubectl apply -f namespace.yaml
kubectl apply -f secret.yaml
kubectl apply -f pvc.yaml

kubectl apply -f storage/zookeeper

echo "Waiting for Zookeeper to be ready..."
kubectl wait --for=condition=Ready pod -l app=zookeeper -n contest --timeout=180s

kubectl apply -f storage/kafka

echo "Waiting for Kafka to be ready..."
kubectl wait --for=condition=Ready pod -l app=kafka -n contest --timeout=180s
kubectl run kafka-client \
  --restart='Never' \
  --image bitnami/kafka:latest \
  --namespace contest \
  --command -- \
  kafka-topics.sh \
    --create \
    --topic submissionTopic \
    --bootstrap-server kafka:9092 \
    --replication-factor 1 \
    --partitions 4

sleep 5

kubectl apply -R -f main
helm install ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx -f helm/ingress-values.yaml
kubectl apply -f ingress.yaml