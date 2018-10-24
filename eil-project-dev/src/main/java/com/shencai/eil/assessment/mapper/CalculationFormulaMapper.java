package com.shencai.eil.assessment.mapper;

import com.shencai.eil.assessment.entity.CalculationFormula;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.CalculationFormulaQueryParam;
import com.shencai.eil.assessment.model.CalculationFormulaVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
public interface CalculationFormulaMapper extends BaseMapper<CalculationFormula> {
    List<CalculationFormulaVO> listFormula(CalculationFormulaQueryParam queryParam);
}
