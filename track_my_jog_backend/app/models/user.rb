class User < ActiveRecord::Base
  # Include default devise modules.
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable
  include DeviseTokenAuth::Concerns::User

  # Associations
  has_many :time_entries

  # Validations
  validates :email, :name, presence: true
  validates :role, inclusion: { in: %w(regular manager admin) }

  def admin?
    self.role.to_sym == :admin
  end

  def manager?
    self.role.to_sym == :manager
  end

  def regular?
    self.role.to_sym == :regular
  end

  def can_crud_role?(role)
    if self.admin?
      true
    elsif self.manager?
      role.to_sym == :manager || role.to_sym == :regular
    else
      role.to_sym == :regular
    end
  end
end
