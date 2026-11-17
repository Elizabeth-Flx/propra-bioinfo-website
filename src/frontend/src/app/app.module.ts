import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {MatToolbarModule} from "@angular/material/toolbar";
import {MatSliderModule} from "@angular/material/slider";
import {SliderComponent} from './components/slider/slider.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {SmallToolsComponent} from './pages/tasks/SmallTasks/small-tools.component';
import {MatTabsModule} from "@angular/material/tabs";
import {HomeComponent} from './pages/home/home.component';
import {AcSearchComponent} from './pages/tasks/SmallTasks/ac-search/ac-search.component';
import {MatInputModule} from "@angular/material/input";
import {PrositeComponent} from './pages/tasks/SmallTasks/prosite/prosite.component';
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {Dna2rnaComponent} from './pages/tasks/SmallTasks/dna2rna/dna2rna.component';
import {GenomeReportComponent} from './pages/tasks/SmallTasks/genome-report/genome-report.component';
import {MatExpansionModule} from "@angular/material/expansion";
import {KeywordSearchComponent} from './pages/tasks/SmallTasks/keyword-search/keyword-search.component';
import {MatChipsModule} from "@angular/material/chips";
import {MatIconModule} from "@angular/material/icon";
import {HomstradComponent} from './pages/tasks/SmallTasks/homstrad/homstrad.component';
import {
  SelectSequenceComponent
} from './pages/tasks/SecondaryStructure/execution/select-sequence/select-sequence.component';
import {SelectModelComponent} from './pages/tasks/SecondaryStructure/execution/select-model/select-model.component';
import {
  SelectReferenceComponent
} from './pages/tasks/SecondaryStructure/execution/select-reference/select-reference.component';
import {CreateModelFileComponent} from './pages/tasks/SecondaryStructure/create-model-file/create-model-file.component';
import {
  PredictionVisualComponent
} from './pages/tasks/SecondaryStructure/execution/prediction-visual/prediction-visual.component';
import {DsspComponent} from './pages/tasks/SecondaryStructure/dssp/dssp.component';
import {MatTableModule} from "@angular/material/table";
import {MatStepperModule} from "@angular/material/stepper";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {
  AlignmentSequenceSelectComponent
} from './pages/tasks/alignment/Calculation/alignment-sequence-select/alignment-sequence-select.component';
import {
  AlignmentParametersComponent
} from './pages/tasks/alignment/Calculation/alignment-parameters/alignment-parameters.component';
import {
  AlignmentReferenceSelectComponent
} from './pages/tasks/alignment/Calculation/alignment-reference-select/alignment-reference-select.component';
import {
  AlignmentResultsComponent
} from './pages/tasks/alignment/Calculation/alignment-results/alignment-results.component';
import {ExecutionComponent} from './pages/tasks/SecondaryStructure/execution/execution.component';
import {MatCardModule} from "@angular/material/card";
import {SecondaryStructureComponent} from './pages/tasks/SecondaryStructure/secondary-structure.component';
import {AlignmentComponent} from './pages/tasks/alignment/alignment.component';
import {AlignmentPlotsComponent} from './pages/tasks/alignment/alignment-plots/alignment-plots.component';
import {
  PredictionSummaryComponent
} from './pages/tasks/SecondaryStructure/execution/prediction-summary/prediction-summary.component';
import {MatDialogModule} from "@angular/material/dialog";
import { MetricComponent } from './pages/tasks/SecondaryStructure/execution/prediction-summary/metric/metric.component';
import { ValidationComponent } from './pages/tasks/SecondaryStructure/validation/validation.component';
import { ReccursiveComponent } from './pages/tasks/alignment/reccursive/reccursive.component';


@NgModule({
  declarations: [
    AppComponent,
    SliderComponent,
    SmallToolsComponent,
    HomeComponent,
    AcSearchComponent,
    PrositeComponent,
    Dna2rnaComponent,
    GenomeReportComponent,
    KeywordSearchComponent,
    HomstradComponent,
    SelectSequenceComponent,
    SelectModelComponent,
    SelectReferenceComponent,
    CreateModelFileComponent,
    PredictionVisualComponent,
    DsspComponent,
    AlignmentSequenceSelectComponent,
    AlignmentParametersComponent,
    AlignmentReferenceSelectComponent,
    AlignmentResultsComponent,
    ExecutionComponent,
    SecondaryStructureComponent,
    AlignmentComponent,
    AlignmentPlotsComponent,
    PredictionSummaryComponent,
    MetricComponent,
    ValidationComponent,
    ReccursiveComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    HttpClientModule,
    MatSliderModule,
    FormsModule,
    MatButtonModule,
    MatTabsModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonToggleModule,
    MatSlideToggleModule,
    MatExpansionModule,
    MatChipsModule,
    MatIconModule,
    MatStepperModule,
    MatSelectModule,
    MatCheckboxModule,
    MatTableModule,
    MatCardModule,
    MatDialogModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
