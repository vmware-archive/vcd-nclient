### Summary
vcd-nclient aims to be a quick start for using the Notifications feature in [vCloud Director].
The application can connect to an AMQP broker, retrieve notification messages and display
them in table format. It is also able to configure a broker for the purposes of vCloud and thus
vCloud Administrators could start playing with Notifications right away. Vcd-nclient can also
serve as programming example for writing vCD extensions which consume vCloud messages.
The source code is clean and can be easily modified.

### Features
* Retrieve notification messages from an AMQP broker and display them in table format
* XML and JSON syntax highlighting for the message payload
* Provide easy way for configuring an AMQP broker for the purposes of vCD (File -> Prepare Broker)

### Build and run
    mvn clean install
    cd target
    java -jar vcd-nclient-1.2.0.jar

  [vCloud Director]: https://www.vmware.com/products/vcloud-director/overview.html
