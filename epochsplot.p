# Gnuplot script file for plotting data in file "epoch.dat"
# This file is called   epochsplot.p

clear
reset
set key off
set border 3

set boxwidth 1000 absolute
set style fill solid 1.0 noborder
set term png
set output "output.png"
bin_width = 2000;

bin_number(x) = floor (x/bin_width)

rounded(x) = bin_width * (bin_number(x) + 0.5)

set title "Distrobution of Epochs to a succssesful Solve"
set xlabel "Number of Epochs"
set ylabel "Frequency"

plot "epoch.dat" using (rounded($1)):(1) smooth frequency with boxes
