export interface Prediction {
  name: string;
  sequence: string;
  prediction: string;
  reference?: string;
  probabilities: Probability[];
  qH?: number;
  qE?: number;
  qC?: number;
  q3?: number;
  sovH?: number;
  sovC?: number;
  sovE?: number;
  sov?: number;

  color?: Position[];
}

export interface Metric {
  min: number;
  max: number;
  mean: number;
  median: number;
  stdDev: number;
  q5: number;
  q95: number;
  q25: number;
  q75: number;
}

export interface Summary {
  q3: Metric;
  sov: Metric;
  qPerSS: {
    H: Metric;
    E: Metric;
    C: Metric;
  }
  sovPerSS: {
    H: Metric;
    E: Metric;
    C: Metric;
  }
}

export interface Result {
  predictions: Prediction[];
  summary?: Summary;
}

export interface Position {
  aminoAcid: string;
  color: string;
}

export interface Options {
  value: string;
  viewValue: string;
}

export interface Probability {
  C: number;

  H: number;
  E: number;
}
