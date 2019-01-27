#!/bin/bash
set -x
set -e

export DEBIAN_FRONTEND="noninteractive"

# basics - do not modify!
apt-get -qq update
apt-get -qq install \
  software-properties-common \
  python3-software-properties \
  pkg-config \
  wget \
  unzip \
  zip \
  dpkg \
  m4 \
  nano vim \
  ncdu \
  htop \
  x11-apps

# Oracle JDK 11 (LTS)
add-apt-repository ppa:linuxuprising/java
apt-get -qq update
echo oracle-java11-installer shared/accepted-oracle-license-v1-2 select true | debconf-set-selections
echo oracle-java11-installer shared/accepted-oracle-license-v1-2 seen true | debconf-set-selections
apt-get -qq install oracle-java11-installer

# Ant
apt-get -qq install ant

# Maven
apt-get -qq install maven

# Qt
apt-get -qq install cmake libqt4-dev libqt4-designer libqt4-opengl libqt4-svg libqtgui4 libqtwebkit4 libstdc++-4.8-dev g++

# Gradle
GRADLE=gradle-5.1.1
GRADLEZIP=gradle-5.1.1-bin.zip
wget -nv -N https://services.gradle.org/distributions/$GRADLEZIP
unzip -d /opt/ $GRADLEZIP
ln -s /opt/$GRADLE/bin/gradle /usr/local/bin/
rm $GRADLEZIP

# Scala
SCALA=scala-2.12.8.deb
wget -nv -N www.scala-lang.org/files/archive/$SCALA
dpkg -i $SCALA &>/dev/null
apt-get -qq update  &>/dev/null
apt-get -qq install scala 2>/dev/null
rm "$SCALA"

# Scala sbt
echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
apt-get -qq update
apt-get -qq install sbt
sbt update

# OCaml
apt-get -qq install ocaml opam

# Haskell dependencies
apt-get -qq install libgmp-dev libnuma-dev

# for backwards compatability
chown -R vagrant $HOME

# see user-bootstrap.sh for more non-sudo bootstrapping
