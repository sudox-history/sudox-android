cmake_minimum_required(VERSION 3.4)

# Include libraries
include(crypto++/CMakeLists.txt)
include(scipher/CMakeLists.txt)

# Enable stripping
#add_custom_command(TARGET scipher POST_BUILD COMMAND ${CMAKE_STRIP} libcrypto++.a)