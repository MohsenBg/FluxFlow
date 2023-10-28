#include <QtWidgets/QApplication>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMessageBox>
#include <Shell.hpp>

int runApp();

int main(int argc, char* argv[]) {
  Shell shell = new Shell();


  return runApp(argc, argv);
}

int runApp(int argc, char* argv[]) {
  QApplication a(argc, argv);
  QMainWindow mainWindow;
  mainWindow.hide();
  return a.exec();
}
