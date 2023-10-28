filename=Rat
build_dir=$(pwd)/build
[ ! -d $build_dir ] && mkdir $build_dir

cd $build_dir && cmake ../ && make && ./Rat