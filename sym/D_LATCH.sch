v 20210731 2
C 40000 40000 0 0 0 title-B.sym
C 45800 48800 1 0 0 NAND.sym
{
T 46150 49550 5 10 1 1 0 0 1
device=NAND
T 47350 49550 5 10 1 1 0 6 1
refdes=U1
}
C 45800 46200 1 0 0 NAND.sym
{
T 46150 46950 5 10 1 1 0 0 1
device=NAND
T 47350 46950 5 10 1 1 0 6 1
refdes=U2
}
C 48600 46500 1 0 0 NAND.sym
{
T 48950 47250 5 10 1 1 0 0 1
device=NAND
T 50150 47250 5 10 1 1 0 6 1
refdes=U4
}
C 48600 48800 1 0 0 NAND.sym
{
T 48950 49550 5 10 1 1 0 0 1
device=NAND
T 50150 49550 5 10 1 1 0 6 1
refdes=U3
}
C 43300 49200 1 0 0 IPAD.sym
{
T 43384 49421 5 10 0 1 0 0 1
device=IPAD
T 43300 49238 5 10 1 1 0 0 1
refdes=DIN
T 43300 49095 5 6 1 1 0 0 1
pinseq=1
}
C 43300 48600 1 0 0 IPAD.sym
{
T 43384 48821 5 10 0 1 0 0 1
device=IPAD
T 43300 48638 5 10 1 1 0 0 1
refdes=CLK
T 43300 48495 5 6 1 1 0 0 1
pinseq=2
}
C 51600 49200 1 0 0 OPAD.sym
{
T 51902 49418 5 10 0 1 0 0 1
device=OPAD
T 51902 49235 5 10 1 1 0 0 1
refdes=Q
T 51892 49092 5 6 1 1 0 0 1
pinseq=3
}
C 51600 46900 1 0 0 OPAD.sym
{
T 51902 47118 5 10 0 1 0 0 1
device=OPAD
T 51902 46935 5 10 1 1 0 0 1
refdes=Q_N
T 51892 46792 5 6 1 1 0 0 1
pinseq=4
}
N 44200 49300 45800 49300 4
N 47700 49300 48600 49300 4
N 47700 46700 48600 46700 4
N 50500 47000 51600 47000 4
N 51600 49300 50500 49300 4
N 51000 47800 51000 49300 4
N 51000 47800 48600 47800 4
N 48600 47800 48600 47000 4
N 51200 47000 51200 48400 4
N 51200 48400 48600 48400 4
N 48600 48400 48600 49000 4
N 47900 49300 47900 47700 4
N 47900 47700 45600 47700 4
N 45600 47700 45600 46700 4
N 45600 46700 45800 46700 4
N 45000 46400 45800 46400 4
N 45000 46400 45000 49000 4
N 45000 49000 45800 49000 4
N 44200 48700 45000 48700 4
