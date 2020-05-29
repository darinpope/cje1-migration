# To make the endpoint private

https://eksctl.io/usage/vpc-networking/#managing-access-to-the-kubernetes-api-server-endpoints

* `eksctl create cluster -f config-1.16.yaml`
* wait for cluster to create
* `kubectl get nodes`
* Modify config-1.16.yaml and change
  * publicAccess: false
  * privateAccess: true
* `eksctl utils update-cluster-endpoints -f config-1.16.yaml`
  * make sure the changes are what you expect
* `eksctl utils update-cluster-endpoints -f config-1.16.yaml --approve`

https://docs.aws.amazon.com/eks/latest/userguide/cluster-endpoint.html#private-access