import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.DomainCredentials
import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.trilead.ssh2.crypto.Base64
import hudson.util.Secret
import hudson.util.XStream2
import jenkins.model.Jenkins

def instance = Jenkins.get()
def credentials = []

// Copy all domains from the system credentials provider
def systemProvider = instance.getExtensionList(SystemCredentialsProvider.class)
if (!systemProvider.empty) {
    def systemStore = systemProvider.first().getStore()
    for (domain in systemStore.domains) {
        credentials.add(new DomainCredentials(domain, systemStore.getCredentials(domain)))
    }
}

// Copy all domains from all folders
def folderExtension = instance.getExtensionList(FolderCredentialsProvider.class)
if (!folderExtension.empty) {
    def folders = instance.getAllItems(Folder.class)
    def folderProvider = folderExtension.first()
    for (folder in folders) {
        def store = folderProvider.getStore(folder)
        for (domain in store.domains) {
            credentials.add(new DomainCredentials(domain, store.getCredentials(domain)))
        }
    }
}

// The converter ensures that the output XML contains the unencrypted secrets
def converter = new Converter() {
    @Override
    void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.value = Secret.toString(object as Secret)
    }

    @Override
    Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) { null }

    @Override
    boolean canConvert(Class type) { type == Secret.class }
}

def stream = new XStream2()
stream.registerConverter(converter)

// Marshal the list of credentials into XML
def xml = stream.toXML(credentials)
println Base64.encode(xml.bytes)
