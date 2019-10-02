{{range services}}{{range service .Name }}Agent Name:{{.Name}} {{ .Node }}
Agent Description:An agent for testing
Number of Executors:2
Root Directory:/home/jenkins
Usage:MatchingLabels
Launch method:SSH
Host:{{.Address}}
Credentials:testCred
Host Key Verification:ManualTrust
Node Label:testLabel1

{{end}}{{end}}

