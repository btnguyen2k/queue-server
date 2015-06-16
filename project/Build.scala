import sbt._
import Keys._
import play.Project._
import com.typesafe.config._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
    override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
    )
    
    val conf            = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
    val appName         = conf.getString("app.name").toLowerCase().replaceAll("\\W+", "-")
    val appVersion      = conf.getString("app.version")
    
    val _springVersion = "4.1.6.RELEASE"
    val _tscVersion = "0.6.0"

    val appDependencies = Seq(
        "org.slf4j"                  %  "log4j-over-slf4j"       % "1.7.12",
        "org.apache.thrift"          %  "libthrift"              % "0.9.2",
        "com.google.guava"           %  "guava"                  % "18.0",
        "mysql"                      %  "mysql-connector-java"   % "5.1.35",
        "org.postgresql"             %  "postgresql"             % "9.4-1201-jdbc41",
        
        "org.springframework"        %  "spring-beans"           % _springVersion,
        "org.springframework"        %  "spring-expression"      % _springVersion,
        "org.springframework"        %  "spring-jdbc"            % _springVersion,
        "org.apache.commons"         %  "commons-dbcp2"          % "2.1",
        
        "com.github.ddth"            %  "ddth-redis"             % "0.4.0",
        "com.github.ddth"            %  "ddth-tsc"               % _tscVersion,
        "com.github.ddth"            %  "ddth-tsc-redis"         % _tscVersion,
        "com.github.ddth"            %  "ddth-dao"               % "0.4.0.2",
        "com.github.ddth"            %  "ddth-queue"             % "0.2.2.2",

        "com.yammer.metrics"         %  "metrics-core"           % "2.2.0" % "test",
        "com.github.ddth"            %  "ddth-thriftpool"        % "0.1.0" % "test",
        
        "com.github.ddth"            %% "play-module-plommon"    % "0.5.1.5",
        
        filters
    )
    
    var _javaVersion = "1.7"
    
    val main = play.Project(appName, appVersion, appDependencies).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repositories
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in targetted java version
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
}
