cmake_minimum_required(VERSION 3.10)
project(Rat)

find_package(Qt5 COMPONENTS Widgets REQUIRED)

set(CMAKE_CXX_STANDARD 11)

# List all your source files explicitly
set(SOURCES
    src/Main.cpp
    src/Shell.cpp
)

# List all your header files explicitly
set(HEADERS
    include/Shell.hpp
)

# Create the executable with the specified sources and headers
add_executable(Rat ${SOURCES} ${HEADERS})

# Link the executable with Qt5 Widgets
target_link_libraries(Rat PRIVATE Qt5::Widgets)


