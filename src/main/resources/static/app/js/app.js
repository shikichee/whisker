/**
 * Created by yoichi.kikuchi on 2016/01/14.
 *
 * @param chart Chart type. Select the "bubble" or "scatter". (Optional)
 * @param xaxis X-Axis label. (Optional)
 * @param yaxis Y-Axis label. (Optional)
 * @param xaxisTooltip X-Axis tooltip label. (Optional)
 * @param yaxisTooltip Y-Axis tooltip label. (Optional)
 */
var Chart = function (bindto, type, xaxis, yaxis, xaxisTooltip, yaxisTooltip) {
    this.bindto = bindto;
    this.type = type || 'bubble';
    this.xaxis = xaxis || 'Refactoring cost';
    this.yaxis = yaxis || 'Service impact';
    this.tooltip = {
        xaxis: xaxisTooltip || 'Refactoring cost',
        yaxis: yaxisTooltip || 'Service impact'
    };

    this.c3data = null;
    this.chart = null;
    this.default = {
        axis: {
            x: 0,
            y: 0
        }
    };
};

/**
 * Draw chart.
 *
 * data layout requirements:
 * [{
 *   "repository": "REPOS",
 *   "xs": [10, 20],
 *   "ys": [60, 70],
 *   "zs": {
 *     "x10y60": [{
 *       "name": "ClassName1a",
 *       "value": 2
 *     }],
 *     "x20y70": [{
 *       "name": "ClassName2a",
 *       "value": 5
 *     }]
 *   }
 * }]
 *
 * @param data Chart data.
 * @param filterZero (Optional)
 * @param filterOver (Optional)
 */
Chart.prototype.draw = function(data, filterZero, filterOver) {
    var chartType = this.type;
    var xaxisText = this.xaxis;
    var yaxisText = this.yaxis;
    var xaxisTooltipText = this.tooltip.xaxis;
    var yaxisTooltipText = this.tooltip.yaxis;
    var fZero = filterZero || false;
    var fOver = filterOver || false;

    var posNames = {};
    var nameValues = {};

    var xs = {};
    var cols = [];

    var pointR = function(data) {
        var x = data.x ? data.x : 0;
        var y = data.value ? data.value : 0;
        var key = 'x' + x + 'y' + y;

        var nms = posNames[key];
        var max = 0;
        for (var i = 0; i < nms.length; i++) {
            var nm = nms[i];
            var val = nameValues[nm];

            if (max < val.value) {
                max = val.value;
            }
        }

        if (fZero && (x <= 0 || y <= 0 || max <= 0)) {
            return 0;
        }

        if (fOver && (x > 100 || y > 100 || max > 100)) {
            return 0;
        }

        max += 1;
        var radius = max < 100 ? max : 100;
        return chartType === 'bubble' ? radius : 2.5;
    };

    for (var i = 0; i < data.length; i++) {
        var d = data[i];
        var repo = d.repository;

        var xindex = repo;
        var yindex = repo + '_y';
        xs[xindex] = yindex;

        var x = d.ys;
        var y = d.xs;
        var z = d.zs;
        x.unshift(xindex);
        y.unshift(yindex);
        cols.push(x);
        cols.push(y);

        for (var key in z) {
            var zz = z[key];

            for (var j = 0; j < zz.length; j++) {
                var nm = zz[j].name;
                var value = zz[j].value;

                nameValues[nm] = {
                    repository: repo,
                    value: value
                };

                if (!(key in posNames)) {
                    posNames[key] = [];
                }
                if (posNames[key].indexOf(nm) < 0) {
                    posNames[key].push(nm);
                }
            }
        }
    }

    this.c3data = {
        bindto: this.bindto,
        data: {
            xs: xs,
            columns: cols,
            type: 'scatter',
            onclick: function (data, element) {
                this.axis.max({
                    x: data.x * 2 + 1,
                    y: data.value * 2 + 1
                });
            }
        },
        axis: {
            x: {
                min: 0,
                label: {
                    text: xaxisText,
                    position: 'outer'
                },
                tick: {
                    fit: false
                }
            },
            y: {
                min: 0,
                label: {
                    text: yaxisText,
                    position: 'outer-middle'
                }
            }
        },
        point: {
            r: pointR
        },
        tooltip: {
            contents: function (data, defaultTitleFormat, defaultValueFormat, color) {
                var x = data[0].x ? data[0].x : 0;
                var y = data[0].value ? data[0].value : 0;
                var key = 'x' + x + 'y' + y;

                var nms = posNames[key];
                var contents = '<div style="background-color:rgba(255,255,255,0.9); padding:1rem;"><h1 style="font-size:18px;">' +
                    xaxisTooltipText + ' = ' + data[0].x + '<br/>' +
                    yaxisTooltipText + ' = ' + data[0].value + '</h1>';

                for (var i = 0; i < nms.length; i++) {
                    var nm = nms[i];
                    var val = nameValues[nm];

                    contents += '<div><i style="color:' + color(val.repository) + '" class="fa fa-square"></i>' +
                        ' ref=' + val.value + ' ' + nm.replace(/,/, '<br/>') + '</div>';
                }

                contents += '</div>';

                return contents;
            }
        }
    };

    this.chart = c3.generate(this.c3data);
};

Chart.prototype.reset = function() {
    this.chart.internal.config.axis_x_max = undefined;
    this.chart.internal.config.axis_y_max = undefined;
    this.chart.flush();
};
