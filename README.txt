SVG
===

Known issues
============

* Gradient transformations are not properly supported yet. There are some
  heuristic-based workarounds, but if skew or scale is part of an SVG gradient
  transformation the result shape is not accurately created. This is mainly
  due to JavaFX's lack of gradient transformation support.

* Sometimes SVG authoring tools save text as <flowRoot> element, not as <text>.
  Apache Batik does not seem to support <flowRoot> - a workaround is to convert
  the <flowRoot> to a <text> element.
  See also http://graphicdesign.stackexchange.com/questions/21662/how-does-inkscape-decide-whether-to-use-flowroot-or-text
