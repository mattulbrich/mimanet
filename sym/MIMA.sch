v 20210731 2
C 40000 40000 0 0 0 title-B.sym
N 40900 45300 56300 45300 10
{
T 55900 45350 5 10 1 1 0 0 1
netname=bus
}
N 45200 44500 45200 45300 10
C 43000 43400 1 0 0 REG.sym
{
T 43350 44750 5 10 1 1 0 0 1
device=REG
T 44550 44750 5 10 1 1 0 6 1
refdes=ACCU
}
N 43000 44500 42700 44500 10
N 42700 44500 42700 45300 10
N 45200 44500 44900 44500 10
C 48500 45100 1 0 0 BUSTAP.sym
{
T 48592 45192 5 2 1 1 0 4 1
device=BUSTAP
T 48642 45242 5 5 1 1 0 0 1
refdes=T3
T 48700 45050 5 10 1 1 0 0 1
spec=0-19
}
N 51300 44500 50900 44500 19
N 49000 44500 48600 44500 19
N 48600 44500 48600 45100 19
C 54100 42600 1 0 0 DUALREG.sym
{
T 54450 44850 5 10 1 1 0 0 1
device=DUALREG
T 55750 44850 5 10 1 1 0 6 1
refdes=RAM
}
N 54100 43700 53200 43700 10
N 53200 42200 53200 43900 10
N 56000 44300 56400 44300 10
N 56400 44300 56400 42200 10
N 56400 42200 53200 42200 10
N 54100 44600 53500 44600 10
N 53500 44600 53500 45300 10
N 56000 44600 56200 44600 10
N 56200 44600 56200 45300 10
C 47900 42700 1 0 0 AND.sym
{
T 48250 43450 5 10 1 1 0 0 1
device=AND
T 49450 43450 5 10 1 1 0 6 1
refdes=U7
}
N 49800 43200 49950 43200 4
N 49950 43200 49950 43700 4
C 51300 44400 1 0 0 OPAD.sym
{
T 51602 44618 5 10 0 1 0 0 1
device=OPAD
T 51602 44635 5 10 1 1 0 0 1
refdes=RAM_ADDR
T 51592 44292 5 6 1 1 0 0 1
pinseq=0
T 51425 44525 5 6 1 1 0 3 1
width=20
}
C 51500 43800 1 0 0 IOPAD.sym
{
T 51684 44021 5 10 0 1 0 0 1
device=IOPAD
T 51500 44038 5 10 1 1 0 0 1
refdes=RAM_VAL
T 51600 43695 5 6 1 1 0 0 1
pinseq=0
T 52375 43925 5 6 1 1 0 3 1
width=24
}
N 52500 43900 53200 43900 4
C 51300 43300 1 0 0 OPAD.sym
{
T 51602 43518 5 10 0 1 0 0 1
device=OPAD
T 51602 43335 5 10 1 1 0 0 1
refdes=RAM_R
T 51592 43192 5 6 1 1 0 0 1
pinseq=0
T 51425 43425 5 6 1 1 0 3 1
width=1
}
C 51300 42800 1 0 0 OPAD.sym
{
T 51602 43018 5 10 0 1 0 0 1
device=OPAD
T 51602 42835 5 10 1 1 0 0 1
refdes=RAM_W
T 51592 42692 5 6 1 1 0 0 1
pinseq=0
T 51425 42925 5 6 1 1 0 3 1
width=1
}
B 51100 42500 1700 2400 12 10 1 0 -1 -1 0 -1 -1 -1 -1 -1
T 51100 42100 12 10 1 0 0 0 2
RAM Interface

C 43600 42100 1 0 0 DRIVER24.sym
{
T 43950 42550 5 10 1 1 0 0 1
device=DRIVER24
T 45150 42550 5 10 1 1 0 6 1
refdes=U5
}
C 41100 40200 1 0 0 ONE.sym
{
T 41450 40650 5 10 1 1 0 0 1
device=ONE
T 42650 40650 5 10 1 1 0 6 1
refdes=U4
}
C 43000 41000 1 0 1 NOT.sym
{
T 42650 41450 5 10 1 1 0 6 1
device=NOT
T 41450 41450 5 10 1 1 0 0 1
refdes=U2
}
C 41100 41700 1 0 0 DUP23.sym
{
T 41450 42150 5 10 1 1 0 0 1
device=DUP23
T 42650 42150 5 10 1 1 0 6 1
refdes=U3
}
N 43000 40400 43000 41200 4
N 41100 41200 41100 41900 4
N 43600 42300 43500 42300 10
N 43500 42300 43500 40000 10
C 43200 41900 1 0 0 BUSTAP.sym
{
T 43292 41992 5 2 1 1 0 4 1
device=BUSTAP
T 43342 42042 5 5 1 1 0 0 1
refdes=T1
T 43300 41750 5 10 1 1 0 0 1
spec=1-23
}
C 43200 40400 1 0 0 BUSTAP.sym
{
T 43292 40492 5 2 1 1 0 4 1
device=BUSTAP
T 43342 40542 5 5 1 1 0 0 1
refdes=T2
T 43400 40350 5 10 1 1 0 0 1
spec=0
}
N 43000 40400 43300 40400 4
N 43300 41900 43000 41900 4
N 45500 42300 45600 42300 10
N 45600 42300 45600 45300 10
C 49000 44000 1 0 0 D_FF20.sym
{
T 49350 44750 5 10 1 1 0 0 1
device=D_FF20
T 50550 44750 5 10 1 1 0 6 1
refdes=U?
}
C 48200 47300 1 0 0 D_FF24.sym
{
T 48550 48050 5 10 1 1 0 0 1
device=D_FF24
T 49750 48050 5 10 1 1 0 6 1
refdes=RX
}
C 50900 47300 1 0 0 D_FF24.sym
{
T 51250 48050 5 10 1 1 0 0 1
device=D_FF24
T 52450 48050 5 10 1 1 0 6 1
refdes=RY
}
C 48200 46000 1 0 0 AND.sym
{
T 48550 46750 5 10 1 1 0 0 1
device=AND
T 49750 46750 5 10 1 1 0 6 1
refdes=U?
}
C 50900 46000 1 0 0 AND.sym
{
T 51250 46750 5 10 1 1 0 0 1
device=AND
T 52450 46750 5 10 1 1 0 6 1
refdes=U?
}
N 43000 44200 42600 44200 4
{
T 42900 44250 5 10 1 1 0 6 1
netname=clk
}
N 48200 46500 47800 46500 4
{
T 48100 46550 5 10 1 1 0 6 1
netname=clk
}
N 50900 46500 50500 46500 4
{
T 50800 46550 5 10 1 1 0 6 1
netname=clk
}
N 47900 43200 47500 43200 4
{
T 47800 43250 5 10 1 1 0 6 1
netname=clk
}
N 48200 47800 47500 47800 10
N 47500 47800 47500 45300 10
C 50900 48500 1 0 0 ALU.sym
{
T 51250 49550 5 10 1 1 0 0 1
device=ALU
T 52450 49550 5 10 1 1 0 6 1
refdes=ALU
}
N 50100 47800 50100 49000 10
N 50100 49000 50900 49000 10
N 50100 46500 50100 47000 4
N 50100 47000 49150 47000 4
N 52800 47800 52800 48300 10
N 52800 48300 50700 48300 10
N 50700 48300 50700 48700 10
N 50700 48700 50900 48700 10
N 50900 47800 50300 47800 10
N 50300 47800 50300 45300 10
N 52800 46500 52800 47000 4
N 52800 47000 51850 47000 4
C 53400 47600 1 0 0 DRIVER24.sym
{
T 53750 48050 5 10 1 1 0 0 1
device=DRIVER24
T 54950 48050 5 10 1 1 0 6 1
refdes=RZ
}
N 53400 47800 53100 47800 10
N 53100 47800 53100 49300 10
N 53100 49300 52800 49300 10
N 53600 47300 54350 47300 4
{
T 53700 47350 5 10 1 1 0 0 1
netname=d_alu
}
N 55300 47800 55300 45300 10
N 45300 41800 44550 41800 4
{
T 45200 41850 5 10 1 1 0 6 1
netname=d_one
}
N 42500 43600 43000 43600 4
{
T 42600 43650 5 10 1 1 0 0 1
netname=d_accu
}
N 44900 44200 45400 44200 10
C 44900 44000 1 0 0 BUSTAP.sym
{
T 44992 44092 5 2 1 1 0 4 1
device=BUSTAP
T 45042 44142 5 5 1 1 0 0 1
refdes=T?
T 45100 43950 5 10 1 1 0 0 1
spec=23
}
N 45000 44000 45000 43100 4
N 45000 43100 44000 43100 4
{
T 44900 43150 5 10 1 1 0 6 1
netname=accu_sign
}
N 42500 43900 43000 43900 4
{
T 42600 43950 5 10 1 1 0 0 1
netname=s_accu
}
N 47500 42900 47900 42900 4
{
T 47600 42950 5 10 1 1 0 0 1
netname=s_adr
}
N 47800 46200 48200 46200 4
{
T 47900 46250 5 10 1 1 0 0 1
netname=s_rx
}
N 50900 46200 50500 46200 4
{
T 50800 46250 5 10 1 1 0 6 1
netname=s_ry
}
N 51300 43400 50300 43400 4
{
T 50900 43450 5 10 1 1 0 6 1
netname=ram_r
}
N 51300 42900 50300 42900 4
{
T 51000 42950 5 10 1 1 0 6 1
netname=ram_w
}
N 54100 44300 53300 44300 4
{
T 54000 44350 5 10 1 1 0 6 1
netname=d_ram
}
N 54100 44000 53300 44000 4
{
T 54000 44050 5 10 1 1 0 6 1
netname=s_ram
}
N 54100 43400 53300 43400 4
{
T 54100 43450 5 10 1 1 0 6 1
netname=d_ramval
}
N 54100 43100 53300 43100 4
{
T 54100 43150 5 10 1 1 0 6 1
netname=s_ramval
}
N 54100 42800 53300 42800 4
{
T 54000 42850 5 10 1 1 0 6 1
netname=clk
}
