const env = process.env.NODE_ENV || 'local';

// Change environment for local
const environment = "-dev" ;
const config = {

  'all': {
    minAreaSize: 10
  },

  'local': {
    root: 'http://localhost:3000/',
    mediaMetadata: 'https://everest' + environment+ '.mcmcg.com/media-metadata/',
    profileService: 'https://everest' + environment+ '.mcmcg.com/media-profiles/',
    utilitiesService: 'localhost:8080/media-utilities/',
    ingestionState: 'https://everest' + environment+ '.mcmcg.com/ingestion-states/',
    ingestionService: 'https://everest' + environment+ '.mcmcg.com/ingestion-workflow-manager/',
    portfolioService: 'https://phxiodpsgwd01.internal.mcmcg.com:7505/portfolios/',
    ingestionStatus: 'https://everest' + environment+ '.mcmcg.com/document-processor/',
    oktaService: 'https://encorecapital.okta.com'
  },

  'build': {
    mediaMetadata: '/media-metadata/',
    profileService: '/media-profiles/',
    utilitiesService: '/media-utilities/',
    ingestionService: '/ingestion-workflow-manager/',
    ingestionState: '/ingestion-states/',
    portfolioService: '/portfolios/',
    ingestionStatus: '/document-processor/',
    oktaService: 'https://encorecapital.okta.com',
  }
}

const envConfig = Object.assign(config.all, config[env === 'local' ? 'local' : 'build']);


export default envConfig;
