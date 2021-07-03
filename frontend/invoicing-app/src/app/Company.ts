export class Company {

  public editMode: boolean = false;
  public editedCompany = null;

  constructor(
    public id: number,
    public taxIdentificationNumber: string,
    public address: string,
    public name: string,
    public pensionInsurance: number,
    public healthInsurance: number
  ) {
  }
}
