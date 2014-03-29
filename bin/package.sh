#!/bin/bash
# NOTE: substitution does not work as expected under dash

# Package up the build, including:
# - check and confirm the version number (against that in setup.py and build.xml)
# - ensure everything checked in to git
# - build the java module
# - build the documentation
# - copy documentation, samples etc. to the output/dist directory
# - build the egg
#

#set -xv

ANT_BUILD_FILE=build.xml

SETUP_PY_FILE=setup.py
SPHINX_CONFIG_FILE=resource/docs/source/conf.py

PACKAGE_OUTPUT_DIR=target/package

# ============================================================
function usage() {
    cat << EOM
Build and package clichart into zip (binary only, not source)
EOM
    exit 1
}

# ============================================================
function checkGitStatus() {
    # disallow if haven't checked everything in
    git status | grep -q "nothing to commit"
    if [ $? -ne 0 ]; then
        echo "You still have uncommitted changes in git"
        echo
        echo "Are you SURE you want to continue? [y/N]"
        read okToContinue
        if [ "$okToContinue" != "y" ]; then
            echo "User cancel"
            exit
        fi
    fi
}

# ============================================================
function getVersionNumber() {
    # determine the current version number, and set as $versionNum
    # Note that this is taken from build.xml, and checked against the shell and batch files
    versionNum=`grep "property.*name=.clichart-version" $ANT_BUILD_FILE \
            | awk '{print substr($3, 7)}' | sed -e 's/"//g'`
    echo "OK to package using existing version $versionNum? [y/N]"
    read okToContinue
    if [ "$okToContinue" != "y" ]; then
        echo "User cancel"
        exit
    fi
}

# ============================================================
function checkVersionNumber() {
    # Check $versionNum against setup.py
    setupVersionNum=`egrep "^ *version ?=" $SETUP_PY_FILE \
            | sed -e 's/^[^"]*"//' -e 's/".*//'`
    if [ "$versionNum" != "$setupVersionNum" ]; then
        echo "Version number ($setupVersionNum) does not match in file $SETUP_PY_FILE"
        exit
    fi

    # Check $versionNum against Sphinx version numbers (x 2)
    sphinxVersionNum=`egrep "^ *version ?=" $SPHINX_CONFIG_FILE \
            | sed -e "s/^[^']*'//" -e "s/'.*//"`
    if [ "$versionNum" != "$sphinxVersionNum" ]; then
        echo "Version number ($sphinxVersionNum) does not match in file $SPHINX_CONFIG_FILE"
        exit
    fi
    sphinxReleaseNum=`egrep "^ *release ?=" $SPHINX_CONFIG_FILE \
            | sed -e "s/^[^']*'//" -e "s/'.*//"`
    if [ "$versionNum" != "$sphinxReleaseNum" ]; then
        echo "Release number ($sphinxReleaseNum) does not match in file $SPHINX_CONFIG_FILE"
        exit
    fi
}

# ============================================================
function checkUnitTests() {
    echo "Running unit tests"
    ant run-tests
}

# ============================================================
function createEgg() {
    # build the .egg file
    eggDir=$1

    echo "Building with ant"
    ant clean package -Dno-tests=true

    echo "Copying files for egg"
    mkdir -p $eggDir
    cp -a target/java/dist/* $eggDir
    cp -a $SETUP_PY_FILE $eggDir

    # scripts
    mkdir $eggDir/clichart
    cp -a src/main/python/*.py $eggDir/clichart
    mv $eggDir/clichart*.jar $eggDir/clichart
    
    # lib directory needs to be under clichart
    mv $eggDir/lib $eggDir/clichart
    
    echo "Creating egg"
    (cd $eggDir; ./setup.py bdist_egg)
    
    echo "Ensuring egg works on all Python versions"
    eggFile=`ls -1 -t $eggDir/dist/*.egg | head -1`
    requiredEggName=`echo $eggFile | sed -e 's/-py2.[4-9]//'`
    if [ "$eggFile" != "$requiredEggName" ]; then
        echo "  Renaming egg from $eggFile to $requiredEggName"
        mv $eggFile $requiredEggName
    fi
}
    
# ============================================================
function createPackageTree() {
    # copy all the required files to the packaging dir, ready to be zipped
    baseDir=$1
    eggDir=$2

    echo "Generating documentation"
    bin/buildDoco.sh

    # docs
    mkdir -p $baseDir/docs
    cp -a target/docs/* $baseDir/docs

    # samples
    mkdir $baseDir/samples
    cp -a resource/samples/*.csv $baseDir/samples
    cp -a resource/samples/*.txt $baseDir/samples
    cp -a resource/samples/*.log $baseDir/samples
    
    # misc
    cp CHANGES.txt README.txt LICENCE.txt INSTALL.txt $baseDir
    cp $eggDir/dist/*.egg $baseDir
}

# ============================================================
function package() {
    clichartName=$1
    (cd $PACKAGE_OUTPUT_DIR; zip -r $clichartName.zip $clichartName)
}

# ============================================================
cd `dirname $0`/..

if [ "$1" = "-h" ]; then
    usage
fi

# sets as $versionNum
getVersionNumber

checkVersionNumber

checkGitStatus

checkUnitTests

eggDir=$PACKAGE_OUTPUT_DIR/clichart-egg
createEgg $eggDir

clichartName=clichart-$versionNum
baseDir=$PACKAGE_OUTPUT_DIR/$clichartName
createPackageTree $baseDir $eggDir

package $clichartName

echo "Done - remember to tag in git!"
