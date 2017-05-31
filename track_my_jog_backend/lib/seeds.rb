require 'factory_girl'

module Seeds
  class SampleData
    def self.run
      create_admin
      create_managers
      create_regulars
    end

    def self.create_admin
      FactoryGirl.create(:admin_user, email: 'admin@example.com', password: 'password')
    end

    def self.create_managers
      3.times do
        user = FactoryGirl.create(:manager_user, password: 'password')
        rand(0..5).times do
          FactoryGirl.create(:time_entry, user: user)
        end
      end
    end

    def self.create_regulars
      5.times do
        user = FactoryGirl.create(:user, password: 'password')
        rand(0..5).times do
          FactoryGirl.create(:time_entry, user: user)
        end
      end
    end
  end
end
