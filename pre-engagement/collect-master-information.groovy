import com.cloudbees.opscenter.server.model.*
import com.cloudbees.jce.masterprovisioning.mesos.MesosMasterProvisioning
println "name,disk,cpus,memory,ratio,image"
Jenkins.instance.getAllItems(ManagedMaster.class).each{ managedMaster ->
  MesosMasterProvisioning kmp = (MesosMasterProvisioning) managedMaster.getConfiguration();
  println managedMaster.name+","+kmp.disk+","+kmp.cpus+","+kmp.memory+","+kmp.ratio+","+kmp.image
}
return
