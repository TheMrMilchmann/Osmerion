#!/bin/bash

SLUG="TheMrMilchmann/Osmerion"
JDK="oraclejdk9"
BRANCH="master"

set -e

if [ "$TRAVIS_REPO_SLUG" == "$SLUG" ] && [ "$TRAVIS_JDK_VERSION" == "$JDK" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "$BRANCH" ]; then
#    # Upload snapshot artifacts to OSSRH.
#
#    echo -e "[deploy.sh] Publishing snapshots...\n"
#
#    ./gradlew uploadArchives --parallel -Psnapshot
#
#    echo -e "[deploy.sh] Published snapshots to OSSRH.\n"

    # Upload latest documentation to Github pages.

    echo -e "[deploy.sh] Publishing documentation...\n"

    ./gradlew aggregateDocs --parallel -Psnapshot

    cp -R build/docs/html $HOME/docs-latest

    pushd $HOME
    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "travis-ci"
    git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/TheMrMilchmann/Osmerion gh-pages > /dev/null

    pushd gh-pages
    git rm -rf .
    cp -Rf $HOME/docs-latest/. .
    git add -f .
    git commit -m "ci: update documentation (travis build $TRAVIS_BUILD_NUMBER)"
    git push -fq origin gh-pages > /dev/null
    popd

    popd

    echo -e "[deploy.sh] Published documentation to gh-pages.\n"
fi