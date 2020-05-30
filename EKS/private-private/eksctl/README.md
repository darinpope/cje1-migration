# Overview

In this directory there are a number of different configuration files. 

Note: If you are not deploying into `us-east-1`, you'll need to update the data in:

* `metadata.region`
* `vpc.subnets.private`

to the region that you are installing into.

# To make the control plane endpoint private

https://eksctl.io/usage/vpc-networking/#managing-access-to-the-kubernetes-api-server-endpoints

With eksctl, you can't initially create a cluster with the endpoint private because the worker nodes will not be able to connect. So, you'll first have to create it public and then update it to be private. The following steps walk you through that process:

* `eksctl create cluster -f config-1.16.yaml`
* wait for cluster to create
* `kubectl get nodes`
  * you should see worker nodes listed
* Modify config-1.16.yaml and change
  * `publicAccess: false`
  * `privateAccess: true`
* `eksctl utils update-cluster-endpoints -f config-1.16.yaml`
  * make sure the changes are what you expect
* `eksctl utils update-cluster-endpoints -f config-1.16.yaml --approve`
  * this will take a few minutes
* `kubectl get nodes`
  * this should time out
* Add the bastion IP to the inbound "...cluster-ControlPlaneSecurityGroup..." security group with HTTPS/443. There should already be two other inbound HTTPS/443 rules in place. This will be the third one.

"You must ensure that your Amazon EKS control plane security group contains rules to allow ingress traffic on port 443 from your bastion host."

https://docs.aws.amazon.com/eks/latest/userguide/cluster-endpoint.html#private-access

# Test manually scale up and down of nodegroup after changing to private

* `eksctl scale nodegroup --cluster=[cluster-id] --nodes=2 cloudbees-core-regular`
* `eksctl scale nodegroup --cluster=[cluster-id] --nodes=1 cloudbees-core-regular`