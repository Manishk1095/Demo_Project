## Run the Registry
#### Windows
```
start rmiregistry
```
#### MacOS
```
rmiregistry
```

## Compile Tracker and Game
```
cd <to/your/root/project/directory>
javac -d out/ ./src/Game/*.java src/Tracker/*.java
```

## Execute Tracker
```
cd out
```
```
start java -classpath "path/to/out/directory" -Djava.rmi.server.codebase="file:/path/to/out/directory/" Tracker.Tracker <tracker_port> <N> <K>
```
#### Example:
```
cd out
```
```
start java -classpath "C:\Users\tioqy\source\MComp - General Track\Semester 1\CS5223 - DISTRIBUTED SYSTEMS\Playground\MazeGame\out" -Djava.rmi.server.codebase="file:/C:\Users\tioqy\source\MComp - General Track\Semester 1\CS5223 - DISTRIBUTED SYSTEMS\Playground\MazeGame\out/" Tracker.Tracker 1099 15 5
```

## Execute Game:
```
cd out
```
```
java Game.Game localhost 1099 AA
java Game.Game localhost 1099 BB
java Game.Game localhost 1099 CC
java Game.Game localhost 1099 DD
```

## Additional Commands
#### Windows
Check rmiregistry is running:
```
netstat -aon | findstr 1099
tasklist | findstr <PID> 
```