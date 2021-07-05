import { ElementFinder, WebElement, by } from "protractor";

export class CompanyRow {
    constructor(private companyRow: ElementFinder){
    }

     deleteBtn(): WebElement {
        return this.companyRow.element(by.css('.btn-danger'))
    }

   async assertRowValues(taxId: string, name: string, address: string, pensionInsurance: string, healthInsurance: string) {
        expect (await this.findTaxId()).toEqual(taxId);
        expect (await this.findName()).toEqual(name);
        expect (await this.findAddress()).toEqual(address);
        expect (await this.findPensionInsurance()).toEqual(pensionInsurance);
        expect (await this.findHealthInsurance()).toEqual(healthInsurance);
    }

    async findTaxId(): Promise<string>  {
            return this.companyRow.element(by.id('taxId')).getText();
        }

    async findName(): Promise<string>  {
            return this.companyRow.element(by.id('name')).getText();
        }

    async findAddress(): Promise<string>  {
            return this.companyRow.element(by.id('address')).getText();
        }

    async findPensionInsurance(): Promise<string>  {
            return this.companyRow.element(by.id('pensionInsurance')).getText();
        }

    async findHealthInsurance(): Promise<string>  {
            return this.companyRow.element(by.id('healthInsurance')).getText();
        }  
        
    async updateCompany(taxId: string, name: string, address: string, pensionInsurance: string, healthInsurance: string) {
        await this.editBtn().click() 
    
        await this.updateTaxIdInput().clear()
        await this.updateTaxIdInput().sendKeys(taxId)    
    
        await this.updateNameInput().clear()
        await this.updateNameInput().sendKeys(name)
    
        await this.updateAddressInput().clear()
        await this.updateAddressInput().sendKeys(address)
    
        await this.updatePensionInsuranceInput().clear()
        await this.updatePensionInsuranceInput().sendKeys(pensionInsurance)
    
        await this.updateHealthInsuranceInput().clear()
        await this.updateHealthInsuranceInput().sendKeys(healthInsurance)
    
        await this.confirmUpdateCompanyBtn().click()
        
        }
        
        private editBtn() {
            return this.companyRow.element(by.id("editBtn"))
        }

        private updateTaxIdInput() {
            return this.companyRow.element(by.css(('input[name=taxIdentificationNumber]')));
        }

        private updateNameInput() {
            return this.companyRow.element(by.css('input[name=name]'))
        }
        
        private updateAddressInput() {
            return this.companyRow.element(by.css('input[name=address]'))
        }
    
        private updatePensionInsuranceInput() {
            return this.companyRow.element(by.css('input[name=pensionInsurance]'))
        }
    
        private updateHealthInsuranceInput() {
            return this.companyRow.element(by.css('input[name=healthInsurance]'))
        }
        
        private confirmUpdateCompanyBtn() {
            return this.companyRow.element(by.id("updateCompanyBtn"))
        }

        
    }
