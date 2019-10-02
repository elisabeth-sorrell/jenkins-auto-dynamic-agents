import hudson.plugins.sshslaves.verifiers.*
import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry



def dataList = []
def theInfoName = '/var/jenkins_home/agent_defs.config'

File theInfoFile = new File( theInfoName )

if( !theInfoFile.exists() ) {
  println "File does not exist"
} else {
  def driveInfo = [:]
  // Step through each line in the file
  theInfoFile.eachLine { line ->
    // If the line isn't blank
    if( line.trim() ) {
      // Split into a key and value
      def (key,value) = line.split( ':' ).collect { it.trim() }
      // and store them in the driveInfo Map
      driveInfo."$key" = value
    }
    else {
      // If the line is blank, and we have some info
      if( driveInfo ) {
        // store it in the list
        dataList << driveInfo
        // and clear it
        driveInfo = [:]
      }
    }
  }
  // when we've finished the file, store any remaining data
  if( driveInfo ) {
    dataList << driveInfo
  }
}

// Go through the map and configure the agent
dataList.eachWithIndex { it, index ->
  println "Agent $index"

  it.get("Agent Name")

  // Set the verification strategy
  SshHostKeyVerificationStrategy hostKeyVerificationStrategy
  switch(it.get("Host Key Verification")) {
      case ~/KnownHostFile/:
        hostKeyVerificationStrategy = new KnownHostsFileKeyVerificationStrategy()
        break
      case ~/ManualTrust/:
        hostKeyVerificationStrategy = new ManuallyTrustedKeyVerificationStrategy(false /*requires initial manual trust*/)
        break
      case ~/NoVerify/:
        hostKeyVerificationStrategy = new NonVerifyingKeyVerificationStrategy()
        break
      // by default, have manually trusted key verification strategy
      default:
        hostKeyVerificationStrategy = ManuallyTrustedKeyVerificationStrategy(false)
        break
      // TODO: implement a manualprovide later and have the user provide another string for it
      //      case ~/ManualProvide/:
      //        break
  }
    
    // Define a "Launch method": "Launch slave agents via SSH"
    ComputerLauncher launcher = new hudson.plugins.sshslaves.SSHLauncher(
            it.get("Host"), // Host
            22, // Port
            it.get("Credentials"), // Credentials
            (String)null, // JVM Options
            (String)null, // JavaPath
            (String)null, // Prefix Start Slave Command
            (String)null, // Suffix Start Slave Command
            (Integer)null, // Connection Timeout in Seconds
            (Integer)null, // Maximum Number of Retries
            (Integer)null, // The number of seconds to wait between retries
            hostKeyVerificationStrategy // Host Key Verification Strategy
    )


    // Define a "Permanent Agent"
    Slave agent = new DumbSlave(
            it.get("Agent Name"),       // Name of the Agent
            it.get("Root Directory"),   // the root directory of the agent
            launcher)
    agent.nodeDescription = it.get("Agent Description")
    agent.numExecutors = it.get("Number of Executors")
    agent.labelString = it.get("Node Label")
    agent.mode = Node.Mode.NORMAL
    agent.retentionStrategy = new RetentionStrategy.Always()

    // TODO: leave this for the day when we get smarter...
    // List<Entry> env = new ArrayList<Entry>();
    // env.add(new Entry("key1","value1"))
    // env.add(new Entry("key2","value2"))
    // EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);
    //agent.getNodeProperties().add(envPro)

    boolean agentExists = false
    Jenkins.instance.computers.each { c ->
      if (c.getNode().getNodeName() == it.get("Agent Name")) {
        agentExists = true
      } 
    }

    if(!agentExists){
      println("Agent does NOT exist. Creating...")
        // Create a "Permanent Agent"
        Jenkins.instance.addNode(agent)
    }
}

// Now that we've covered ADDING in agents that DON'T exist on the Jenkins side, but DO exist in the 
//    config file, we need now to DELETE agents in Jenkins that DON'T exist in the config file.

// Soo, loop through the jenkins agents...

Jenkins.instance.computers.each { c ->
   existsInConfig = false

   dataList.each { it ->
      if(c.getNode().getNodeName() == it.get("Agent Name")) {
        existsInConfig = true
      }
   }

   if(!existsInConfig) {
       println(c.getNode().getNodeName() + " does NOT exist in config file. Deleting....")
       Jenkins.instance.removeNode(c.getNode())
   }
}







