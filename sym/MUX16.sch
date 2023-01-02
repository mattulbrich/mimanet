v 20210731 2
C 40000 40000 0 0 0 title-B.sym
C 42000 49500 1 0 0 IPAD.sym
{
T 42084 49721 5 10 0 1 0 0 1
device=IPAD
T 42000 49538 5 10 1 1 0 0 1
refdes=SEL
T 42000 49395 5 6 1 1 0 0 1
pinseq=17
T 42775 49625 5 6 1 1 0 3 1
width=4
T 41200 49800 5 10 1 0 0 0 1
pintype=control
}
N 42900 49600 48500 49600 10
C 43700 49400 1 0 0 BUSTAP.sym
{
T 43792 49492 5 2 1 1 0 4 1
device=BUSTAP
T 43842 49542 5 5 1 1 0 0 1
refdes=T1
T 43900 49350 5 10 1 1 0 0 1
spec=0-1
}
C 47300 49400 1 0 0 BUSTAP.sym
{
T 47392 49492 5 2 1 1 0 4 1
device=BUSTAP
T 47442 49542 5 5 1 1 0 0 1
refdes=T3
T 47500 49350 5 10 1 1 0 0 1
spec=2-3
}
C 44000 47200 1 0 0 MUX4.sym
{
T 44350 48550 5 10 1 1 0 0 1
device=MUX4
T 45550 48550 5 10 1 1 0 6 1
refdes=U1
}
N 42900 48300 44000 48300 4
N 42900 48000 44000 48000 4
N 44000 47700 42900 47700 4
N 42900 47400 44000 47400 4
N 43800 46900 44950 46900 4
C 44000 45200 1 0 0 MUX4.sym
{
T 44350 46550 5 10 1 1 0 0 1
device=MUX4
T 45550 46550 5 10 1 1 0 6 1
refdes=U2
}
N 42900 46300 44000 46300 4
N 42900 46000 44000 46000 4
N 44000 45700 42900 45700 4
N 42900 45400 44000 45400 4
N 43800 44900 44950 44900 4
C 44000 41200 1 0 0 MUX4.sym
{
T 44350 42550 5 10 1 1 0 0 1
device=MUX4
T 45550 42550 5 10 1 1 0 6 1
refdes=U5
}
N 42900 42300 44000 42300 4
N 42900 42000 44000 42000 4
N 44000 41700 42900 41700 4
N 42900 41400 44000 41400 4
N 43800 40900 44950 40900 4
C 44000 43200 1 0 0 MUX4.sym
{
T 44350 44550 5 10 1 1 0 0 1
device=MUX4
T 45550 44550 5 10 1 1 0 6 1
refdes=U4
}
N 42900 44300 44000 44300 4
N 42900 44000 44000 44000 4
N 44000 43700 42900 43700 4
N 42900 43400 44000 43400 4
N 43800 42900 44950 42900 4
N 43800 49400 43800 40900 4
C 47800 47200 1 0 0 MUX4.sym
{
T 48150 48550 5 10 1 1 0 0 1
device=MUX4
T 49350 48550 5 10 1 1 0 6 1
refdes=U3
}
N 45900 48300 47800 48300 4
N 47800 48000 46300 48000 4
N 46300 48000 46300 46300 4
N 46300 46300 45900 46300 4
N 47800 47700 46700 47700 4
N 46700 47700 46700 44300 4
N 46700 44300 45900 44300 4
N 47800 47400 47100 47400 4
N 47100 47400 47100 42300 4
N 47100 42300 45900 42300 4
N 47400 49400 47400 46900 4
N 47400 46900 48750 46900 4
C 50100 48200 1 0 0 OPAD.sym
{
T 50402 48418 5 10 0 1 0 0 1
device=OPAD
T 50402 48235 5 10 1 1 0 0 1
refdes=OUT
T 50392 48092 5 6 1 1 0 0 1
pinseq=0
T 50225 48325 5 6 1 1 0 3 1
width=1
}
N 49700 48300 50100 48300 4
C 42000 48200 1 0 0 IPAD.sym
{
T 42084 48421 5 10 0 1 0 0 1
device=IPAD
T 42000 48238 5 10 1 1 0 0 1
refdes=IN1
T 42000 48095 5 6 1 1 0 0 1
pinseq=1
T 42775 48325 5 6 1 1 0 3 1
width=1
}
C 42000 47900 1 0 0 IPAD.sym
{
T 42084 48121 5 10 0 1 0 0 1
device=IPAD
T 42000 47938 5 10 1 1 0 0 1
refdes=IN2
T 42000 47795 5 6 1 1 0 0 1
pinseq=2
T 42775 48025 5 6 1 1 0 3 1
width=1
}
C 42000 47600 1 0 0 IPAD.sym
{
T 42084 47821 5 10 0 1 0 0 1
device=IPAD
T 42000 47638 5 10 1 1 0 0 1
refdes=IN3
T 42000 47495 5 6 1 1 0 0 1
pinseq=3
T 42775 47725 5 6 1 1 0 3 1
width=1
}
C 42000 47300 1 0 0 IPAD.sym
{
T 42084 47521 5 10 0 1 0 0 1
device=IPAD
T 42000 47338 5 10 1 1 0 0 1
refdes=IN4
T 42000 47195 5 6 1 1 0 0 1
pinseq=4
T 42775 47425 5 6 1 1 0 3 1
width=1
}
C 42000 46200 1 0 0 IPAD.sym
{
T 42084 46421 5 10 0 1 0 0 1
device=IPAD
T 42000 46238 5 10 1 1 0 0 1
refdes=IN5
T 42000 46095 5 6 1 1 0 0 1
pinseq=5
T 42775 46325 5 6 1 1 0 3 1
width=1
}
C 42000 45900 1 0 0 IPAD.sym
{
T 42084 46121 5 10 0 1 0 0 1
device=IPAD
T 42000 45938 5 10 1 1 0 0 1
refdes=IN6
T 42000 45795 5 6 1 1 0 0 1
pinseq=6
T 42775 46025 5 6 1 1 0 3 1
width=1
}
C 42000 45600 1 0 0 IPAD.sym
{
T 42084 45821 5 10 0 1 0 0 1
device=IPAD
T 42000 45638 5 10 1 1 0 0 1
refdes=IN7
T 42000 45495 5 6 1 1 0 0 1
pinseq=7
T 42775 45725 5 6 1 1 0 3 1
width=1
}
C 42000 45300 1 0 0 IPAD.sym
{
T 42084 45521 5 10 0 1 0 0 1
device=IPAD
T 42000 45338 5 10 1 1 0 0 1
refdes=IN8
T 42000 45195 5 6 1 1 0 0 1
pinseq=8
T 42775 45425 5 6 1 1 0 3 1
width=1
}
C 42000 44200 1 0 0 IPAD.sym
{
T 42084 44421 5 10 0 1 0 0 1
device=IPAD
T 42000 44238 5 10 1 1 0 0 1
refdes=IN9
T 42000 44095 5 6 1 1 0 0 1
pinseq=9
T 42775 44325 5 6 1 1 0 3 1
width=1
}
C 42000 43900 1 0 0 IPAD.sym
{
T 42084 44121 5 10 0 1 0 0 1
device=IPAD
T 42000 43938 5 10 1 1 0 0 1
refdes=IN10
T 42000 43795 5 6 1 1 0 0 1
pinseq=10
T 42775 44025 5 6 1 1 0 3 1
width=1
}
C 42000 43600 1 0 0 IPAD.sym
{
T 42084 43821 5 10 0 1 0 0 1
device=IPAD
T 42000 43638 5 10 1 1 0 0 1
refdes=IN11
T 42000 43495 5 6 1 1 0 0 1
pinseq=11
T 42775 43725 5 6 1 1 0 3 1
width=1
}
C 42000 43300 1 0 0 IPAD.sym
{
T 42084 43521 5 10 0 1 0 0 1
device=IPAD
T 42000 43338 5 10 1 1 0 0 1
refdes=IN12
T 42000 43195 5 6 1 1 0 0 1
pinseq=12
T 42775 43425 5 6 1 1 0 3 1
width=1
}
C 42000 42200 1 0 0 IPAD.sym
{
T 42084 42421 5 10 0 1 0 0 1
device=IPAD
T 42000 42238 5 10 1 1 0 0 1
refdes=IN13
T 42000 42095 5 6 1 1 0 0 1
pinseq=13
T 42775 42325 5 6 1 1 0 3 1
width=1
}
C 42000 41900 1 0 0 IPAD.sym
{
T 42084 42121 5 10 0 1 0 0 1
device=IPAD
T 42000 41938 5 10 1 1 0 0 1
refdes=IN14
T 42000 41795 5 6 1 1 0 0 1
pinseq=14
T 42775 42025 5 6 1 1 0 3 1
width=1
}
C 42000 41600 1 0 0 IPAD.sym
{
T 42084 41821 5 10 0 1 0 0 1
device=IPAD
T 42000 41638 5 10 1 1 0 0 1
refdes=IN15
T 42000 41495 5 6 1 1 0 0 1
pinseq=15
T 42775 41725 5 6 1 1 0 3 1
width=1
}
C 42000 41300 1 0 0 IPAD.sym
{
T 42084 41521 5 10 0 1 0 0 1
device=IPAD
T 42000 41338 5 10 1 1 0 0 1
refdes=IN16
T 42000 41195 5 6 1 1 0 0 1
pinseq=16
T 42775 41425 5 6 1 1 0 3 1
width=1
}
