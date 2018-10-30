# -- coding: utf-8 --
from __future__ import division
from math import e, pi, sqrt, log
import sys


# 河流完全混合模式
def River_Completely_Mixed(Ch, m, Qh):
    c = (m + Ch * Qh) / Qh
    return c


# 二维稳态混合模式（岸边排放）
def Two_Steady_Shore(x, y, Ch, m, H, My, u, B):
    part1 = e ** ((u * y ** 2) / (4 * My * x))
    part2 = e ** (-u * ((2 * B - y) ** 2) / (4 * My * x))
    c = Ch + m / (H * sqrt(pi * My * x * u)) * (part1 + part2)
    return c


# 二维稳态混合模型（非岸边排放）
def Two_Steady_Nonshore(x, y, Ch, m, H, My, u, B, a):
    part1 = e ** (-u * y ** 2 / (4 * My * x))
    part2 = e ** (-u * (2 * a + y) ** 2 / (4 * My * x))
    part3 = e ** (-u * (2 * B - 2 * a - y) ** 2 / (4 * My * x))
    c = Ch + m / (2 * H * sqrt(pi * My * x * u)) * (part1 + part2 + part3)
    return c


# 二维稳态混合衰减模式（岸边排放）
def Two_Steady_Attenuation_Shore(x, y, Ch, m, H, My, u, B, K1):
    part1 = e ** (-K1 * x / (86400 * u))
    part2 = e ** (-u * y ** 2 / (4 * My * x))
    part3 = e ** (-u * (2 * B - y) ** 2 / (4 * My * x))
    c = part1 * (Ch + m / (H * sqrt(pi * My * x * u)) * (part2 + part3))
    return c


# 二维稳态混合衰减模式（非岸边排放）
def Two_Steady_Attenuation_Nonshore(x, y, Ch, m, H, My, u, B, a, K1):
    part1 = e ** (-K1 * x / (86400 * u))
    part2 = e ** (-u * y ** 2 / (4 * My * x))
    part3 = e ** (-u * (2 * a + y) ** 2 / (4 * My * x))
    part4 = e ** (-u * (2 * B - 2 * a - y) ** 2 / (4 * My * x))
    c = part1 * (Ch + m / (2 * H * sqrt(pi * My * x * u)) * (part2 + part3 + part4))
    return c


# 托马斯模式
def Thomas_Mode(x, Ch, m, Qh, u, K1, K3):
    c0 = (m + Ch * Qh) / Qh
    c = c0 * e ** (-(K1 + K3) * x / (86400 * u))
    return c


# 欧康纳河口衰减模式（上溯时&下泄时）
def OConnor_Attenuation(x, Ch, m, Qh, Ml, u, K1):
    M = sqrt(1 + 4 * K1 * Ml / u ** 2)
    if x <= 0:
        c = m/ (Qh * M) * e ** (u * x / (2 * Ml) * (1 + M)) + Ch
    if x > 0:
        c = m / ((Qh) * M) * e ** (u * x / (2 * Ml) * (1 - M)) + Ch
    return c


# 湖泊稳态二维环流混合模式（岸边排放）
def Lake_Two_Steady_Shore(x, y, Ch, m, H, My, u):
    part1 = e ** (-x * y ** 2 / (4 * My * x))
    c = Ch + m / (H * sqrt(pi * My * x * u)) * part1
    return c


# 湖泊稳态二维环流混合模式（非岸边排放）
def Lake_Two_Steady_Nonshore(x, y, Ch,m, H, My, u, a):
    part1 = e ** (-x * y ** 2 / (4 * My * x))
    part2 = e ** (-u * (2 * a + y) ** 2 / (4 * My * x))
    c = Ch +m / (2 * H * sqrt(pi * My * x * u)) * (part1 + part2)
    return c


# 湖泊稳态二维混合衰减混合模式（岸边排放）
def Lake_Two_Steady_Attenuation_Shore(x, y, Ch, m, H, My, u, K1):
    part1 = e ** (-u * y ** 2 / (4 * My * x))
    part2 = e ** (-K1 * x / (86400 * u))
    c = (Ch + m / (H * sqrt(pi * My * x * u)) * part1) * part2
    return c


# 湖泊稳态二维混合衰减混合模式（非岸边排放）
def Lake_Two_Steady_Attenuation_Nonshore(x, y, Ch, m, H, My, u, a, K1):
    part1 = e ** (-x * y ** 2 / (4 * My * x))
    part2 = e ** (-u * (2 * a + y) ** 2 / (4 * My * x))
    part3 = e ** (-K1 * x / (86400 * u))
    c = (Ch + m / (2 * H * sqrt(pi * My * x * u)) * (part1 + part2)) * part3
    return c


# 费伊(Fay)油膜扩延公式
def Fay_Expansion(V):
    D = 356.8 * V ** (3 / 8)
    return D


if __name__ == '__main__':
    Function = sys.argv[1]
    para = sys.argv[2:]
    para = map(float, para)
    if Function == 'River_Completely_Mixed':
        c = River_Completely_Mixed(*para)
    if Function == 'Two_Steady_Shore':
        c = Two_Steady_Shore(*para)
    if Function == 'Two_Steady_Nonshore':
        c = Two_Steady_Nonshore(*para)
    if Function == 'Two_Steady_Attenuation_Shore':
        c = Two_Steady_Attenuation_Shore(*para)
    if Function == 'Two_Steady_Attenuation_Nonshore':
        c = Two_Steady_Attenuation_Nonshore(*para)
    if Function == 'OConnor_Attenuation':
        c = OConnor_Attenuation(*para)
    if Function == 'Lake_Two_Steady_Shore':
        c = Lake_Two_Steady_Shore(*para)
    if Function == 'Lake_Two_Steady_Nonshore':
        c = Lake_Two_Steady_Nonshore(*para)
    if Function == 'Lake_Two_Steady_Attenuation_Shore':
        c = Lake_Two_Steady_Attenuation_Shore(*para)
    if Function == 'Lake_Two_Steady_Attenuation_Nonshore':
        c = Lake_Two_Steady_Attenuation_Nonshore(*para)
    if ('c' in vars()):
        print c
    if Function == 'Thomas_Mode':
        c, D, xc = Thomas_Mode(*para)
        print c, D, xc
    if Function == 'Fay_Expansion':
        D = Fay_Expansion(*para)
        print D
