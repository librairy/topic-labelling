nohup ./mvn-test.sh  WikiCreateIntTest > nohup.log 2>&1 &
echo $! > nohup.pid
tail -f nohup.log