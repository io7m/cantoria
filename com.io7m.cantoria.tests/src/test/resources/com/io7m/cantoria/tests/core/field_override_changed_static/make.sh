#!/usr/bin/env bash
set -e
set -x

JAVAC="/usr/lib/jvm/java-9-openjdk/bin/javac"
JAVA="/usr/lib/jvm/java-9-openjdk/bin/java"

rm -rf classes R.java S.java T.java

(cat <<EOF
module x {
  exports x;
}
EOF
) | tee module-info.java

(cat <<EOF
package x;

public class R
{
  public R()
  {

  }

  public static void main(String args[])
  {
    T t = new T();
    System.out.println(t.f);
  }
}
EOF
) | tee R.java

(cat <<EOF
package x;

public class S
{
  protected int f = 32;

  public S()
  {

  }
}
EOF
) | tee S.java

(cat <<EOF
package x;

public class T extends S
{
  public T()
  {

  }
}
EOF
) | tee T.java

mkdir -p classes
${JAVAC} -d classes T.java S.java R.java module-info.java

pushd classes && faketime '2000-01-01T00:00:00Z' jar -cf ../before/module.jar . && popd

(cat <<EOF
package x;

public class T extends S
{
  protected static int f = 64;

  public T()
  {

  }
}
EOF
) | tee T.java

${JAVAC} -d classes T.java S.java

pushd classes && faketime '2000-01-01T00:00:00Z' jar -cf ../after/module.jar . && popd

rm -rfv *.java classes

