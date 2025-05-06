#!/bin/bash

mvn clean package
cp -v openxava/target/openxava-*.jar ox-application/web/WEB-INF/lib/
cp -v openxava-addons/target/openxava-addons-*.jar ox-application/web/WEB-INF/lib/
cp -v mahaswami/target/mahaswami-*.jar ox-application/web/WEB-INF/lib/
sh ox-application/target/bin/webapp