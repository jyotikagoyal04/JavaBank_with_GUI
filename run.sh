#!/bin/bash
echo "Compiling JavaBank..."
mkdir -p out
javac -d out -sourcepath src \
  src/BankingApp.java \
  src/model/*.java \
  src/service/*.java \
  src/util/*.java \
  src/gui/*.java

if [ $? -ne 0 ]; then
  echo "❌ Compilation failed."
  exit 1
fi

echo "✅ Compiled successfully."
echo ""

if [ "$1" == "--cli" ]; then
  echo "Starting JavaBank (CLI mode)..."
  java -cp out BankingApp --cli
else
  echo "Starting JavaBank (GUI mode)..."
  java -cp out BankingApp
fi
