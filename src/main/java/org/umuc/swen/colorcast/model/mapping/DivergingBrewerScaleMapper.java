package org.umuc.swen.colorcast.model.mapping;

import java.awt.Color;
import java.util.List;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.jcolorbrewer.ColorBrewer;
import org.umuc.swen.colorcast.CyActivator;
import org.umuc.swen.colorcast.model.exception.InvalidDataException;

/**
 * Created by cwancowicz on 10/17/16.
 */
public class DivergingBrewerScaleMapper<T extends Number> extends VisualStyleFilterMapper {

  private final Double maxValue;
  private final Integer colorScale;

  public DivergingBrewerScaleMapper(String columnName, ColorBrewer colorBrewer, Class<T> type, List<T> values,
                                    CyActivator cyActivator) {
    super(cyActivator, columnName, type);
    this.maxValue = getMaxValue(values);
    this.colorScale = 100;
    initializeBoundaryRanges(colorBrewer);
  }

  @Override
  public MapType getMapType() {
    return MapType.DIVERGING;
  }

  private void initializeBoundaryRanges(ColorBrewer colorBrewer) {
    // create a color scale with 100 "negative colors" 1 color for zero and 100 "positive colors"
    Color[] colors = colorBrewer.getColorPalette((colorScale * 2) + 1);
    ((ContinuousMapping) visualMappingFunction).addPoint(maxValue * -1, new BoundaryRangeValues(colors[0], colors[0], colors[1]));
    ((ContinuousMapping) visualMappingFunction).addPoint(0, new BoundaryRangeValues(colors[colorScale], colors[colorScale], colors[colorScale]));
    ((ContinuousMapping) visualMappingFunction).addPoint(maxValue, new BoundaryRangeValues(colors[colors.length-2], colors[colors.length-1], colors[colors.length-1]));
  }

  @Override
  protected VisualMappingFunction createVisualMappingFunction() {
    return this.cyActivator.getVmfFactoryContinuous().createVisualMappingFunction(columnName,
            type, BasicVisualLexicon.NODE_FILL_COLOR);
  }

  private Double getMaxValue(List<T> values) {
  return values.stream()
            .max((row1, row2) -> Double.valueOf(Math.abs(row1.doubleValue()))
                    .compareTo(Double.valueOf(Math.abs(row2.doubleValue()))))
            .orElseThrow(() -> new InvalidDataException(columnName)).doubleValue();
  }
}
