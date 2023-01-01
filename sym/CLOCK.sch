v 20210731 2
C 40000 40000 0 0 0 title-B.sym
C 46500 48200 1 0 0 OPAD.sym
{
T 46802 48418 5 10 0 1 0 0 1
device=OPAD
T 46802 48235 5 10 1 1 0 0 1
refdes=CLK
T 46792 48092 5 6 1 1 0 0 1
pinseq=0
T 46625 48325 5 6 1 1 0 3 1
width=1
}
C 42900 47900 1 0 0 IMPLEMENTATION.sym
{
T 44400 48600 5 10 1 1 0 5 1
device=IMPLEMENTATION
T 45850 48750 5 10 1 1 0 6 1
refdes=IMPL
T 43000 48050 5 10 1 0 0 0 1
class=Clock
}
T 42900 47300 9 10 1 0 0 0 2
factor=<n> to slow the clock
invert=true to invert the clock
