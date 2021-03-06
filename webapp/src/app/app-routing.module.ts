import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { ResultsComponent } from './results/results.component';
import { ResultsResolver } from './results/results.resolver';
import { SearchComponent } from './search/search.component';

const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'search', component: SearchComponent },
  { path: 'results', component: ResultsComponent, resolve: { results: ResultsResolver } },
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
