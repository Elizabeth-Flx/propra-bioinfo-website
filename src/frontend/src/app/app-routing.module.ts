import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SmallToolsComponent} from "./pages/tasks/SmallTasks/small-tools.component";
import {HomeComponent} from "./pages/home/home.component";
import {SecondaryStructureComponent} from "./pages/tasks/SecondaryStructure/secondary-structure.component";
import {AlignmentComponent} from "./pages/tasks/alignment/alignment.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'smallTools', component: SmallToolsComponent},
  {path: 'secondary', component: SecondaryStructureComponent},
  {path: 'alignment', component: AlignmentComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
