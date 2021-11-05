mvn package
ls target | grep .jar | xargs -i cp target/{} demo-server/plugins/{}
cd demo-server
java -Xmx4096M -Xms1024M -jar server.jar nogui
