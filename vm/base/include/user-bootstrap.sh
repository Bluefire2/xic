#!/bin/bash
set -x
set -e

command_exists () {
    type "$1" &> /dev/null ;
}

# put or link binaries here
BIN=$HOME/bin

# put things that should only be run once here
MARK=$HOME/.initialized
if [ ! -e $MARK ] ; then
  # add $BIN to PATH
  mkdir -p $BIN
  echo "PATH+=:$BIN" >> $HOME/.profile

  # nice prompt
  cat /vagrant/include/profile >> $HOME/.profile

  # Jflex
  JFLEX="jflex-1.6.1"
  ARCHIVE="$JFLEX.tar.gz"
  wget -nv -N http://jflex.de/release/$ARCHIVE
  tar -zxf $ARCHIVE
  rm $ARCHIVE
  ln -sf $HOME/$JFLEX/bin/jflex $HOME/bin
  echo -e "\nexport JFLEX_HOME=$HOME/jflex/" >> $HOME/.profile

  # OCaml opam
  if command_exists opam ; then
    opam init -y #&>/dev/null
    eval `opam config env`
    echo '' >> $HOME/.profile
    echo '#OPAM' >> $HOME/.profile
    echo 'eval `opam config env`' >> $HOME/.profile

    # Oasis
    opam install oasis -y &>/dev/null
  fi

  # Haskell - install ghcup, GHC, and cabal-install
  mkdir -p ~/.ghcup/bin
  curl -sSL https://raw.githubusercontent.com/haskell/ghcup/master/ghcup > ~/.ghcup/bin/ghcup
  chmod +x ~/.ghcup/bin/ghcup
  PATH+=:"$HOME/.cabal/bin:$HOME/.ghcup/bin:$PATH"
  echo 'PATH+=:"$HOME/.cabal/bin:$HOME/.ghcup/bin:$PATH"' >> $HOME/.profile
  ghcup install
  ghcup set
  ghcup install-cabal
  cabal new-update

  # Haskell stack - install to $BIN
  curl -sSL https://get.haskellstack.org/ | sh -s - -d $BIN

  # insecure SSH setup
  # https://www.vagrantup.com/docs/boxes/base.html
  curl -sSL https://github.com/hashicorp/vagrant/raw/master/keys/vagrant.pub >> ~/.ssh/authorized_keys

  touch $MARK
  echo "VAGRANT: DO NOT REMOVE" >> $MARK
fi

WEBSEMESTER=2019sp

#Everything below will be used later in the semester

# test harness - do not modify!
XTH=xth.tar.gz
wget -nv -N http://www.cs.cornell.edu/courses/cs4120/$WEBSEMESTER/project/$XTH
mkdir -p xth
tar -zxf $XTH -C xth --owner=vagrant
ln -sf $HOME/xth/xth $BIN

## runtime libraries - do not modify!
#XIRT=pa5-release.zip
#wget -nv -N -q http://www.cs.cornell.edu/courses/cs4120/$WEBSEMESTER/pa/pa5/$XIRT && \
#  mkdir -p runtime && \
#  unzip -q $XIRT "pa5_student/runtime/*" -d runtime && \
#  (cd runtime ; cp -frp pa5_student/runtime/* . ; rm -rf pa5_student ; make) || \
#true
#
## QtXi libraries - do not modify!
#XIRT=pa7-release.zip
#wget -nv -N -q http://www.cs.cornell.edu/courses/cs4120/$WEBSEMESTER/pa/pa7/$XIRT && \
#  mkdir -p runtime && \
#  unzip -q $XIRT "pa7_student/QtXi/*" -d QtXi && \
#  (cd QtXi ; cp -frp pa7_student/QtXi/* . ; rm -rf pa7_student ; make) || \
#true
