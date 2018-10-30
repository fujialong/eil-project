# -- coding: utf-8 --
import numpy as np
import time
import sys

e = 2.71828183  # 自然对数

# '''
# 参数输入顺序：
# 基础参数
# 1、土壤含水率(theta)
# 2、弥散系数（Dij）
# 3、渗透系数（K）
# 4、土壤密度（rho）
# 5、土壤-水分配系数（Kd）
# 6、一阶吸附解吸速率常数（k）
# 7、水相一阶微生物降解速率常数（mu_c)
# 8、吸附相的一阶微生物降解速率系数（mu_s）
# 9、初始溶质质量,单位g（M）
#
# 预测参数
# 10、坐标x
# 11、坐标y
# 12、坐标z
# 13、时间T
# '''

def Model(theta,Dij,K,rho,Kd,k,mu_c,mu_s,M,x0,y0,z0,T):

    # 初始化方程中的参数，单位参照 假设数据1011.docx
    # theta = 0.15  # 土壤含水率
    # Dij = 1.26 * 10 ** (-2)  # 弥散系数
    # K=1.728 #渗透系数
    # K = 10 ** (-2)
    # rho = 1.2  # 土壤密度
    # Kd = 0.63  # 土壤-水分配系数
    # k = 6.94 * 10 ** (-6)  # 一阶吸附解吸速率常数
    # mu_c = 0.008  # 水相一阶微生物降解速率常数
    # mu_s = 0.004  # 吸附相的一阶微生物降解速率系数

    qi = lambda x, y, z: K * z / (x ** 2 + y ** 2 + z ** 2 + 0.0001) ** (0.5) * int(z > 0)  # 达西流速，z是距离地表的深度,地表以上认为qi=0
    # qi=lambda x,y,z : 0.01

    # 一些简化记号
    D = theta * Dij
    lamb = rho * theta * k * Kd + theta * mu_c
    a = rho * theta * k
    b = theta * k * Kd
    mu = mu_s + theta * k

    # 初始溶质质量,单位g
    # M = 200

    # 这是样例输入，实际使用的时候要根据实际初始值手动初始化.
    # 比如初始污染如果是Dirac函数，建议用Guass函数近似，便于计算。
    get_init_C = lambda x, y, z: M * 125 / (2 * 3.1416) ** 1.5 * e ** (-12.5 * (x * x + y * y + z * z))
    # get_init_S=lambda x,y,z : M*10**3*125/(2*3.1416)**1.5*e**(-12.5*(x*x+y*y+z*z))*2*int(z>0)


    # 要预测的位置
    # x0 = 0.5
    # y0 = 0.5
    # z0 = 0.15
    #
    # # 要预测的时间，单位d，初始时间为0
    # T = 20
    T = round(T) + 1

    # 用差分代替微分
    dt = 0.2
    dh = 0.1
    n = int(2 * T / dt + 1)

    # 定义C,S初始值，初始污染如果是Dirac函数，建议用Guass函数近似，便于计算。
    C0 = np.zeros((n, n, n))
    C1 = np.zeros((n, n, n))
    S0 = np.zeros((n, n, n))
    for x in range(n):
        for y in range(n):
            for z in range(n):
                C0[x, y, z] = get_init_C((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0, (z - int(n / 2)) * dh + z0)
                C1[x, y, z] = C0[x, y, z]
                S0[x, y, z] = C0[x, y, z]

    flag = 0

    # print("初始浓度为{}\n".format(C0[int(n / 2), int(n / 2), int(n / 2)]))

    rawsum = C0.sum()

    '''
    算法思路：只考虑要预测的点附近区域的浓度变化；这里取边长为n*dh的立方体
    算法没有考虑污染物传播到地表后不再向上传播的情况，猜测这种情况下，向上传播的量很少，可以忽略不计
    '''

    for t in range(2, int(n / 2)):
        if flag == 0:
            flag = 1 - flag
            for x in range(t, n - t):
                for y in range(t, n - t):
                    for z in range(t, n - t):
                        S0[x, y, z] = e ** (-mu * dt) * (S0[x, y, z] + b * C0[x, y, z] * dt)

                        Cxx = (C0[x + 2, y, z] + C0[x - 2, y, z] - 2 * C0[x, y, z]) / (4 * dh * dh)
                        Cyy = (C0[x, y + 2, z] + C0[x, y - 2, z] - 2 * C0[x, y, z]) / (4 * dh * dh)
                        Czz = (C0[x, y, z + 2] + C0[x, y, z - 2] - 2 * C0[x, y, z]) / (4 * dh * dh)
                        Cxy = (C0[x + 1, y + 1, z] + C0[x - 1, y - 1, z] - C0[x - 1, y + 1, z] - C0[x + 1, y - 1, z]) / (
                                    4 * dh * dh)
                        Cyz = (C0[x, y + 1, z + 1] + C0[x, y - 1, z - 1] - C0[x, y + 1, z - 1] - C0[x, y - 1, z + 1]) / (
                                    4 * dh * dh)
                        Cxz = (C0[x + 1, y, z + 1] + C0[x - 1, y, z - 1] - C0[x - 1, y, z + 1] - C0[x + 1, y, z - 1]) / (
                                    4 * dh * dh)
                        X1 = D / theta * (Cxx + Cyy + Czz + Cxy + Cyz + Cxz)
                        X2 = -(qi((x + 1 - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0, (z - int(n / 2)) * dh + z0) *
                               C0[x + 1, y, z] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C0[x, y, z]) / (dh * theta)
                        X3 = -(qi((x - int(n / 2)) * dh + x0, (y + 1 - int(n / 2)) * dh + y0, (z - int(n / 2)) * dh + z0) *
                               C0[x, y + 1, z] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C0[x, y, z]) / (dh * theta)
                        X4 = -(qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0, (z + 1 - int(n / 2)) * dh + z0) *
                               C0[x, y, z + 1] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C0[x, y, z]) / (dh * theta)

                        C1[x, y, z] = C0[x, y, z] + dt * (X1 + X2 + X3 + X4 - lamb * C0[x, y, z] + a * S0[x, y, z])
                        if C1[x, y, z] < 0:
                            C1[x, y, z] = 0
            newsum = C1.sum()
            for x in range(n):
                for y in range(n):
                    for z in range(n):
                        C1[x, y, z] = C1[x, y, z] * rawsum / newsum

        else:
            flag = 1 - flag
            for x in range(t, n - t):
                for y in range(t, n - t):
                    for z in range(t, n - t):
                        S0[x, y, z] = e ** (-mu * dt) * (S0[x, y, z] + b * C1[x, y, z] * dt)

                        Cxx = (C1[x + 2, y, z] + C1[x - 2, y, z] - 2 * C1[x, y, z]) / (4 * dh * dh)
                        Cyy = (C1[x, y + 2, z] + C1[x, y - 2, z] - 2 * C1[x, y, z]) / (4 * dh * dh)
                        Czz = (C1[x, y, z + 2] + C1[x, y, z - 2] - 2 * C1[x, y, z]) / (4 * dh * dh)
                        Cxy = (C1[x + 1, y + 1, z] + C1[x - 1, y - 1, z] - C1[x - 1, y + 1, z] - C1[x + 1, y - 1, z]) / (
                                    4 * dh * dh)
                        Cyz = (C1[x, y + 1, z + 1] + C1[x, y - 1, z - 1] - C1[x, y + 1, z - 1] - C1[x, y - 1, z + 1]) / (
                                    4 * dh * dh)
                        Cxz = (C1[x + 1, y, z + 1] + C1[x - 1, y, z - 1] - C1[x - 1, y, z + 1] - C1[x + 1, y, z - 1]) / (
                                    4 * dh * dh)
                        X1 = D / theta * (Cxx + Cyy + Czz + Cxy + Cyz + Cxz)
                        X2 = -(qi((x + 1 - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0, (z - int(n / 2)) * dh + z0) *
                               C1[x + 1, y, z] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C1[x, y, z]) / (dh * theta)
                        X3 = -(qi((x - int(n / 2)) * dh + x0, (y + 1 - int(n / 2)) * dh + y0, (z - int(n / 2)) * dh + z0) *
                               C1[x, y + 1, z] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C1[x, y, z]) / (dh * theta)
                        X4 = -(qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0, (z + 1 - int(n / 2)) * dh + z0) *
                               C1[x, y, z + 1] - qi((x - int(n / 2)) * dh + x0, (y - int(n / 2)) * dh + y0,
                                                    (z - int(n / 2)) * dh + z0) * C1[x, y, z]) / (dh * theta)

                        C0[x, y, z] = C1[x, y, z] + dt * (X1 + X2 + X3 + X4 - lamb * C1[x, y, z] + a * S0[x, y, z])
                        if C0[x, y, z] < 0:
                            C0[x, y, z] = 0
            newsum = C0.sum()
            for x in range(n):
                for y in range(n):
                    for z in range(n):
                        C0[x, y, z] = C1[x, y, z] * rawsum / newsum
        tmp = int(1 / dt)
        if t % tmp == 0:
            if flag == 0:
                # print("{}天后的浓度为{}\n".format(int(t / tmp), C1[int(n / 2), int(n / 2), int(n / 2)]))
                C = C1[int(n / 2), int(n / 2), int(n / 2)]
            else:
                # print("{}天后的浓度为{}\n".format(int(t / tmp), C0[int(n / 2), int(n / 2), int(n / 2)]))
                C = C0[int(n / 2), int(n / 2), int(n / 2)]
    return C,S0[int(n / 2), int(n / 2), int(n / 2)]

if __name__ == '__main__':
    para = map(float,sys.argv[1:])
    #$print 'please wait ...'
    c,S0 = Model(*para)
    #print '水相污染物浓度：',c
    print c,S0